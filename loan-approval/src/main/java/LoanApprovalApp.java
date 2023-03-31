import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

public class LoanApprovalApp {

    private static Properties getConfig() {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "loan-approval-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker-1:9092, broker-2:9092");


        // Specify default (de)serializers for record keys and for record values.
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        return config;
    }

    private static Topology getTopology() {
        StreamsBuilder builder;
        KStream<Integer, String> delimTransStream;
        KStream<Integer, String> indTransStream;
        Topology result;

        builder = new StreamsBuilder();

        // Source processor: get stream from Kafka topic
        delimTransStream = builder.stream("some_topic_in");

        // Internal processor: break up the stream
        indTransStream = delimTransStream.mapValues(value -> value.toUpperCase());

        // Sink processor: new stream back to new Kafka topic
        indTransStream.to("some_topic_out");

        // Generate and return topology
        result = builder.build();
        return result;
    }

    public static void main(String[] args) {

        // Create a streams application based on config & topology.
        try (KafkaStreams streams = new KafkaStreams(getTopology(), getConfig())) {

            // Run the Streams application via `start()`
            streams.start();

            // Stop the application gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        }
    }
}
