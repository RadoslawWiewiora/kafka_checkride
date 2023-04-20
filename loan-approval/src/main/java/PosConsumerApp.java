import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class PosConsumerApp {

    private static final Logger log = LoggerFactory.getLogger(PosConsumerApp.class.getSimpleName());
    private static boolean consume = true;
    private static Properties properties;

    public static void main(String[] args) {

        String propertiesFile = args[0];
        properties = Utils.readProperties(propertiesFile);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "POS-Consumers");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "POS-Consumer");

        PosConsumerApp app = new PosConsumerApp();
        app.readLoanDecisions();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer...");
            consume = false;
        }));
    }

    public void readLoanDecisions() {

        final Serde<String> stringSerde = Serdes.String();
        final SpecificAvroSerde<LoanDecision> decisionSerde = Utils.getAvroSerde(properties);

        // create a consumer
        ConsumerRecords<String, LoanDecision> records;
        try (KafkaConsumer<String, LoanDecision> consumer = new KafkaConsumer<>(
                properties, stringSerde.deserializer(), decisionSerde.deserializer() )) {

            while (consume) {

                consumer.subscribe(List.of(Constants.LOAN_DECISIONS_TOPIC));
                records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, LoanDecision> record : records) {
                    logDecision(record.value());
                }
            }
        }
    }

    private void logDecision(LoanDecision decision) {

        // ANSI escape code colors and reset
        final String ANSI_RED = "\033[31m";
        final String ANSI_GREEN = "\033[32m";
        final String ANSI_RESET = "\033[0m";

        log.warn(decision.getApproved() ?
                "DECISION: " + ANSI_GREEN + decision + ANSI_RESET :
                "DECISION: " + ANSI_RED + decision + ANSI_RESET);
    }
}
