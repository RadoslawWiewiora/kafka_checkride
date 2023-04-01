import java.util.Properties;
import java.util.Random;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import pojo.avro.CreditScore;
import pojo.avro.LoanApplication;
import pojo.avro.LoanDecision;

public class LoanApprovalApp {

    private static Properties getConfig() {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "loan-approval-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Specify default (de)serializers for record keys and for record values.
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        return config;
    }

    private static Topology getTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        // Source processor: get loan applications from Kafka topic
        KStream<Integer, LoanApplication> loanApplicationsStream = builder.stream("loan_applications");

        // Internal processor: make decision based on credit score
        KStream<Integer, LoanDecision> decisionStream = loanApplicationsStream
                .mapValues(v -> LoanDecisionMaker.AnalyzeApplication(v, getCreditScore(v)));

        // Sink processor: return decision to new Kafka topic
        decisionStream.to("loan_decisions");

        // Generate and return topology
        return builder.build();
    }

    private static CreditScore getCreditScore(LoanApplication application) {
        Random randomNum = new Random();
        int score = randomNum. nextInt(100);
        return new CreditScore(application.getName(), application.getSurname(), score);
    }

    public static void main(String[] args) {

        System.out.println("Start");

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(), getConfig())) {

            // Run the Streams application via `start()`
            streams.start();

            // Stop the application gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        }

        System.out.println("Done");
    }
}
