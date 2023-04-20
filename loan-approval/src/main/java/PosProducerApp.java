import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanRequest;

import java.util.*;


public class PosProducerApp {

    private static final Logger log = LoggerFactory.getLogger(PosProducerApp.class.getSimpleName());

    private static boolean produce = true;

    private static Properties properties;

    private final Random randomNum = new Random();

    private List<ProducerRecord<String, LoanRequest>> bfbClients;

    public static void main(String[] args) {

        String propertiesFile = args[0];
        properties = Utils.readProperties(propertiesFile);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        PosProducerApp app = new PosProducerApp();
        app.populateBfbClients();
        Scanner scanner = new Scanner(System.in);

        while (produce) {
            app.sendLoanRequestForInternalClient();
            app.sendLoanRequestForExternalClient();
            System.out.println("Produce more?");
            scanner.nextLine();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down producer...");
            produce = false;
            scanner.close();
        }));
    }

    public void sendLoanRequestForInternalClient() {

        try (KafkaProducer<String, LoanRequest> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, LoanRequest> internal = this.bfbClients.get(this.randomNum.nextInt(10));
            producer.send(internal);
            log.warn("REQUEST for: " + internal.value().getName() + " " + internal.value().getSurname());
        }
    }

    public void sendLoanRequestForExternalClient() {

        try (KafkaProducer<String, LoanRequest> producer = new KafkaProducer<>(properties)) {
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
}
