import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;
import pojo.avro.LoanRequest;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class PosLoanApp {

    private static final Logger log = LoggerFactory.getLogger(PosLoanApp.class.getSimpleName());

    public static void main(String[] args) {

        sendLoanRequest();
        readLoanDecision();
    }

    public static void readLoanDecision() {

        // create a consumer
        ConsumerRecords<String, LoanDecision> records;
        try (KafkaConsumer<String, LoanDecision> consumer = new KafkaConsumer<>(getConsumerConfig())) {

            // subscribe to a topic
            consumer.subscribe(List.of(Constants.LOAN_DECISIONS_TOPIC));

            log.info("Waiting for response");

            records = consumer.poll(Duration.ofMillis(1000));
        }

        for (ConsumerRecord<String, LoanDecision> record : records) {
            log.info("LOAN DECISION:");
            log.info("Key: " + record.key() + ", Value: " + record.value());
        }
    }

    public static void sendLoanRequest() {

        // Sample loan request
        Random randomNum = new Random();
        LoanRequest application = new LoanRequest();
        application.setName("John");
        application.setSurname("Smith");
        application.setAmount(randomNum.nextInt(100, 5000));

        // For topic key I would like to use hash code of client PII data
        // Integer key = (application.getName() + application.getSurname()).hashCode();
        String key = "1";
        ProducerRecord<String, LoanRequest> request =
                new ProducerRecord<>(Constants.LOAN_REQUESTS_TOPIC, key, application);

        try (KafkaProducer<String, LoanRequest> producer = new KafkaProducer<>(getProducerConfig())) {
            producer.send(request, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Data not send to Kafka. Error: " + exception.getMessage());
                } else {
                    log.info("Data send to Kafka");
                }
            });
            producer.flush();
        }
    }

    public static Properties getProducerConfig() {
        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        return props;
    }

    public static Properties getConsumerConfig() {
        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "POS-Applications");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "POS-Applications-1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }
}
