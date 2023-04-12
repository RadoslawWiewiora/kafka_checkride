import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
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

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "loan-approval-app");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "loan-approval-app-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    private static Topology getTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        // Source processor: get loan applications from Kafka topic
        KStream<String, LoanRequest> loanApplicationsStream = builder.stream(Constants.LOAN_REQUESTS_TOPIC);

        // Source processor: get internal clients credit scores from Kafka topic
        KTable<String, client_credit_score> internalClientsStream = builder.table(Constants.INTERNAL_CLIENTS_TOPIC);

        // Internal processor: Join loan requests with internal clients credit score
        LoanRequestCreditScoreJoiner joiner = new LoanRequestCreditScoreJoiner();
        KStream<String, LoanRequestsWithCreditScore> joined = loanApplicationsStream
                .leftJoin(internalClientsStream, joiner);

        KStream<String, LoanRequestsWithCreditScore> withoutCreditScore = joined.filter((k, v) -> v.getCreditScoreSource() == null);
        KStream<String, LoanRequestsWithCreditScore> withInternalCreditScore = joined.filter((k, v) -> v.getCreditScoreSource() != null);

        KStream<String, LoanRequestsWithCreditScore> withExternalCreditScore = withoutCreditScore.mapValues(CreditBureauMock::addCreditScore);
        KStream<String, LoanRequestsWithCreditScore> withCreditScore = withInternalCreditScore.merge(withExternalCreditScore);

        // Internal processor: make decision based on credit score
        KStream<String, LoanDecision> decisionStream = withCreditScore
                .mapValues(LoanDecisionMaker::AnalyzeApplication);

        // Sink processor: return decision to new Kafka topic
        decisionStream.to(Constants.LOAN_DECISIONS_TOPIC);

        // Generate and return topology
        return builder.build();
    }

    public static void main(String[] args) {

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(), getConfig())) {

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
