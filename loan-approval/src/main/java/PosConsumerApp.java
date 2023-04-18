import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class PosConsumerApp {

    private static final Logger log = LoggerFactory.getLogger(PosConsumerApp.class.getSimpleName());
    private static boolean consume = true;

    public static void main(String[] args) {

        PosConsumerApp app = new PosConsumerApp();
        app.readLoanDecisions();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer...");
            consume = false;
        }));
    }

    public void readLoanDecisions() {

        // create a consumer
        ConsumerRecords<String, LoanDecision> records;
        try (KafkaConsumer<String, LoanDecision> consumer = new KafkaConsumer<>(getConsumerConfig())) {

            while (consume) {
                // subscribe to a topic
                consumer.subscribe(List.of(Constants.LOAN_DECISIONS_TOPIC));
                records = consumer.poll(Duration.ofMillis(100));
                consumer.commitSync();
                for (ConsumerRecord<String, LoanDecision> record : records) {
                    log.warn("LOAN DECISION: " + record.value());
                }
            }
        }
    }

    public static Properties getConsumerConfig() {
        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "POS-Consumers");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "POS-Consumer");

        return props;
    }
}
