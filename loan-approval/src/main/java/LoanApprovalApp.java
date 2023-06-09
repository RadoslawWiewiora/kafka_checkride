import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;
import pojo.avro.LoanRequest;
import pojo.avro.LoanRequestsWithCreditScore;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class LoanApprovalApp {

    private static final Logger log = LoggerFactory.getLogger(LoanApprovalApp.class.getSimpleName());

    public static Topology getTopology(Properties properties) {

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<String> stringSerde = Serdes.String();
        final SpecificAvroSerde<LoanRequest> requestSerde = Utils.getAvroSerde(properties);
        final SpecificAvroSerde<LoanRequestsWithCreditScore> withScoreSerde = Utils.getAvroSerde(properties);
        final SpecificAvroSerde<LoanDecision> decisionSerde = Utils.getAvroSerde(properties);
        final SpecificAvroSerde<credit_scores> clientSerde = Utils.getAvroSerde(properties);

        // Source processor: get loan applications from Kafka topic
        KStream<String, LoanRequest> loanApplicationsStream = builder.stream(
                Constants.LOAN_REQUESTS_TOPIC,
                Consumed.with(stringSerde, requestSerde).withName("Loan_requests"));

        // Source processor: get internal clients credit scores from Kafka topic
        KTable<String, credit_scores> internalClientsStream = builder.table(
                Constants.INTERNAL_CREDIT_SCORES_TOPIC,
                Consumed.with(stringSerde, clientSerde).withName("BFG_data"));

        // Source processor: get internal clients credit scores from Kafka topic
        KStream<String, LoanRequestsWithCreditScore> externalCreditScores = builder.stream(
                Constants.EXTERNAL_CREDIT_SCORES_TOPIC,
                Consumed.with(stringSerde, withScoreSerde).withName("External_credit_scores"));

        // Internal processor: Join loan requests with BFG clients credit score
        LoanRequestCreditScoreJoiner joiner = new LoanRequestCreditScoreJoiner();
        KStream<String, LoanRequestsWithCreditScore> joined = loanApplicationsStream.leftJoin(
                internalClientsStream,
                joiner,
                Joined.with(stringSerde, requestSerde, clientSerde).withName("join_with_BFB_data"));

        Map<String, KStream<String, LoanRequestsWithCreditScore>> branch = joined.split(Named.as("split_"))
                .branch((k, v) -> v.getCreditScoreSource() == null,
                        Branched.as("without_credit_score"))
                .defaultBranch(Branched.as("BFB_credit_score"));

        KStream<String, LoanRequestsWithCreditScore> withoutCreditScore = branch.get("split_without_credit_score");
        KStream<String, LoanRequestsWithCreditScore> withBFBCreditScore = branch.get("split_BFB_credit_score");

        withoutCreditScore
                .mapValues(CreditBureauMock::addCreditScore, Named.as("Add_credit_bureau_credit_score"))
                .to(Constants.EXTERNAL_CREDIT_SCORES_TOPIC,
                    Produced.with(stringSerde, withScoreSerde).withName(Constants.EXTERNAL_CREDIT_SCORES_TOPIC));

        KStream<String, LoanRequestsWithCreditScore> withCreditScore = withBFBCreditScore
                .merge(externalCreditScores, Named.as("With_credit_score"));

        // Internal processor: make decision based on credit score
        KStream<String, LoanDecision> decisionStream = withCreditScore
                .mapValues(LoanDecisionMaker::AnalyzeApplication, Named.as("With_decisions"))
                .peek((k,v) -> log.warn("DECISION: " + v.toString()));

        // Sink processor: return decision to new Kafka topic
        decisionStream.to(Constants.LOAN_DECISIONS_TOPIC,
                Produced.with(stringSerde, decisionSerde).withName("Loan_decisions"));

        // Generate and return topology
        Topology topology = builder.build();
        log.info("Topology: " + topology.describe().toString());
        return topology;
    }

    public static void main(String[] args) {

        String propertiesFile = args[0];
        Properties properties = Utils.readProperties(propertiesFile);

        // Latch to block the main thread
        final CountDownLatch latch = new CountDownLatch(1);

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(properties), properties)) {

            streams.start();
            latch.await();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                streams.close();
                latch.countDown();
            }));

        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
