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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.CreditScore;
import pojo.avro.LoanApplication;
import pojo.avro.LoanDecision;

import java.time.Duration;
import java.util.Properties;
import java.util.Random;

public class LoanApprovalApp {

    private static final Logger log = LoggerFactory.getLogger(LoanApprovalApp.class.getSimpleName());

    private static Properties getConfig() {

        final Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "loan-approval-app");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "loan-approval-app-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    private static Topology getTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        // Source processor: get loan applications from Kafka topic
        KStream<Integer, LoanApplication> loanApplicationsStream = builder.stream(Constants.LOAN_APPLICATIONS_TOPIC);

        // Internal processor: make decision based on credit score
        KStream<Integer, LoanDecision> decisionStream = loanApplicationsStream
                .mapValues(v -> {
                    log.info("Value from stream: " + v.toString());
                    return v;
                })
                .mapValues(v -> LoanDecisionMaker.AnalyzeApplication(v, getCreditScore(v)));

        // Sink processor: return decision to new Kafka topic
        decisionStream.to(Constants.LOAN_DECISIONS_TOPIC);

        // Generate and return topology
        return builder.build();
    }

    private static CreditScore getCreditScore(LoanApplication application) {
        Random randomNum = new Random();
        int score = randomNum.nextInt(100);
        return new CreditScore(application.getName(), application.getSurname(), score);
    }

    public static void main(String[] args) {

        System.out.println("Start");

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(), getConfig())) {

            // Run the Streams application via `start()`
            streams.start();

            // TODO How to run it locally for some period of time?
            Thread.sleep(Duration.ofMinutes(1).toMillis());

            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done");
    }
}
