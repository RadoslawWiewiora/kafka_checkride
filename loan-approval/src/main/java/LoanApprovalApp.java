import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import model.Constants;
import model.LoanDecisionMaker;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanApplication;
import pojo.avro.LoanApplicationWithCreditScore;
import pojo.avro.LoanDecision;

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
        KStream<String, LoanApplication> loanApplicationsStream = builder.stream(Constants.LOAN_APPLICATIONS_TOPIC);

        // Source processor: get internal clients credit scores from Kafka topic
        KTable<String, client_credit_score> internalClientsStream = builder.table(Constants.INTERNAL_CLIENTS_TOPIC);

        ValueJoiner<LoanApplication, client_credit_score, LoanApplicationWithCreditScore> enrichmentJoiner = (loanApplication, internal_credit_score) -> {
            LoanApplicationWithCreditScore withCreditScore = new LoanApplicationWithCreditScore();
            withCreditScore.setName(loanApplication.getName());
            withCreditScore.setSurname(loanApplication.getSurname());
            if (internal_credit_score != null) {
                withCreditScore.setCreditScore(internal_credit_score.getCreditScore());
            }
            return withCreditScore;
        };

        KStream<String, LoanApplicationWithCreditScore> enrichedApplications = loanApplicationsStream
                .join(internalClientsStream, enrichmentJoiner)
                .peek((key, value) -> log.info("AFTER JOIN key " + key + " value " + value));

        // Internal processor: make decision based on credit score
        KStream<String, LoanDecision> decisionStream = enrichedApplications
                .mapValues(LoanDecisionMaker::AnalyzeApplication);

        decisionStream.peek((k,v) -> log.info("DECISIONS: " + k + " value: " + v));

        // Sink processor: return decision to new Kafka topic
        decisionStream.to(Constants.LOAN_DECISIONS_TOPIC);

        // Generate and return topology
        return builder.build();
    }

    public static void main(String[] args) {

        System.out.println("Start");

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(), getConfig())) {

            // Run the Streams application via `start()`
            streams.start();

            // TODO How to run it locally for some period of time?
            Thread.sleep(Duration.ofSeconds(15).toMillis());

            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
