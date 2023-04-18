import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;
import pojo.avro.LoanRequest;
import pojo.avro.LoanRequestsWithCreditScore;

import java.time.Duration;
import java.util.Properties;

public class LoanApprovalApp {

    private static final Logger log = LoggerFactory.getLogger(LoanApprovalApp.class.getSimpleName());

    private static Properties getConfig() {

        final Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "loan-approval-1");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "loan-approval-client-1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

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
                Consumed.with(stringSerde, clientSerde).withName("BFG_clients"));

        // Source processor: get internal clients credit scores from Kafka topic
        KStream<String, LoanRequestsWithCreditScore> externalCreditScores = builder.stream(
                Constants.EXTERNAL_CREDIT_SCORES_TOPIC,
                Consumed.with(stringSerde, withScoreSerde).withName("External_credit_scores"));

        // Internal processor: Join loan requests with BFG clients credit score
        LoanRequestCreditScoreJoiner joiner = new LoanRequestCreditScoreJoiner();
        KStream<String, LoanRequestsWithCreditScore> joined = loanApplicationsStream.leftJoin(
                internalClientsStream,
                joiner,
                Joined.with(stringSerde, requestSerde, clientSerde).withName("Enriched_with_BFB_data"));

        KStream<String, LoanRequestsWithCreditScore> withoutCreditScore = joined
                .filter((k, v) -> v.getCreditScoreSource() == null, Named.as("Without_credit_score"));
        KStream<String, LoanRequestsWithCreditScore> withInternalCreditScore = joined
                .filter((k, v) -> v.getCreditScoreSource() != null, Named.as("BFB_credit_score"));

        withoutCreditScore
                .mapValues(CreditBureauMock::addCreditScore, Named.as("Add_credit_bureau_credit_score"))
                .to(Constants.EXTERNAL_CREDIT_SCORES_TOPIC,
                    Produced.with(stringSerde, withScoreSerde).withName(Constants.EXTERNAL_CREDIT_SCORES_TOPIC));

        KStream<String, LoanRequestsWithCreditScore> withCreditScore = withInternalCreditScore
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

        Properties properties = getConfig();

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(properties), properties)) {

            // Run the Streams application via `start()`
            streams.start();

            // TODO How to run it locally for some period of time?
            Thread.sleep(Duration.ofMinutes(10).toMillis());

            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
