import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class PosLoanApp {

    private static final Logger log = LoggerFactory.getLogger(PosLoanApp.class.getSimpleName());

    private final Random randomNum = new Random();

    private List<ProducerRecord<String, LoanRequest>> bfbClients;

    public static void main(String[] args) {

        PosLoanApp app = new PosLoanApp();
        app.populateBfbClients();

        app.sendLoanRequestForInternalClient();
        app.sendLoanRequestForExternalClient();

        app.readLoanDecision();
    }

    public void readLoanDecision() {

        // create a consumer
        ConsumerRecords<String, LoanDecision> records;
        try (KafkaConsumer<String, LoanDecision> consumer = new KafkaConsumer<>(getConsumerConfig())) {

            // subscribe to a topic
            consumer.subscribe(List.of(Constants.LOAN_DECISIONS_TOPIC));
            records = consumer.poll(Duration.ofMillis(1000));
        }

        for (ConsumerRecord<String, LoanDecision> record : records) {
            log.warn("LOAN DECISION: " + record.value());
        }
    }

    public void sendLoanRequestForInternalClient() {

        try (KafkaProducer<String, LoanRequest> producer = new KafkaProducer<>(getProducerConfig())) {
            ProducerRecord<String, LoanRequest> internal = this.bfbClients.get(this.randomNum.nextInt(10));
            producer.send(internal);
            log.warn("REQUEST for: " + internal.value().getName() + " " + internal.value().getSurname());
        }
    }

    public void sendLoanRequestForExternalClient() {

        try (KafkaProducer<String, LoanRequest> producer = new KafkaProducer<>(getProducerConfig())) {
            NameGenerator generator = new NameGenerator();
            Name name = generator.generateName();
            String key = String.valueOf(randomNum.nextInt());
            ProducerRecord<String, LoanRequest> external = generateRequest(name.getFirstName(), name.getLastName(), key);
            producer.send(external);
            log.warn("REQUEST for: " + external.value().getName() + " " + external.value().getSurname());
        }
    }

    private ProducerRecord<String, LoanRequest> generateRequest(String name, String surname, String key) {

        // Sample loan request
        LoanRequest application = new LoanRequest();
        application.setName(name);
        application.setSurname(surname);
        application.setAmount(randomNum.nextInt(100, 5000));

        // For topic key I would like to use hash code of client PII data
        // Integer key = (application.getName() + application.getSurname()).hashCode();
        return new ProducerRecord<>(Constants.LOAN_REQUESTS_TOPIC, key, application);
    }

    public void populateBfbClients() {
        this.bfbClients = new ArrayList<>();
        this.bfbClients.add(generateRequest("John", "Smith", "1"));
        this.bfbClients.add(generateRequest("Ann", "Johnson", "2"));
        this.bfbClients.add(generateRequest("Henry", "Smith", "3"));
        this.bfbClients.add(generateRequest("Emile", "Miller", "4"));
        this.bfbClients.add(generateRequest("Mike", "Smith", "5"));
        this.bfbClients.add(generateRequest("John", "Johnson", "6"));
        this.bfbClients.add(generateRequest("Ann", "Miller", "7"));
        this.bfbClients.add(generateRequest("Henry", "Smith", "8"));
        this.bfbClients.add(generateRequest("Emile", "Smith", "9"));
        this.bfbClients.add(generateRequest("Mike", "Johnson", "10"));
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
