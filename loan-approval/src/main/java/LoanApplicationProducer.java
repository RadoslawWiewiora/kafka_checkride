import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import model.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanApplication;

import java.util.Properties;
import java.util.Random;

public class LoanApplicationProducer {

    private static final Logger log = LoggerFactory.getLogger(LoanApplicationProducer.class.getSimpleName());

    public static void main(String[] args) {

        KafkaProducer<Integer, LoanApplication> producer = new KafkaProducer<>(getConfig());

        // Sample Loan application
        Random randomNum = new Random();
        LoanApplication application = new LoanApplication();
        application.setName("John");
        application.setSurname("Smith");
        application.setAmount(randomNum.nextInt(100, 5000));

        // For topic key I would like to use hash code of client PII data
        Integer key = (application.getName() + application.getSurname()).hashCode();

        ProducerRecord<Integer, LoanApplication> producerRecord =
                new ProducerRecord<>(Constants.LOAN_APPLICATIONS_TOPIC, key, application);

        producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Data not send to Kafka. Error: " + exception.getMessage());
            }
            else {
                log.info("Data send to Kafka");
            }
        });
        producer.flush();
        producer.close();
    }

    public static Properties getConfig() {
        final Properties props = new Properties();

        // Add additional properties.
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constants.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        return props;
    }
}
