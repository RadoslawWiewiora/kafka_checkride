import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.Before;
import org.junit.Test;
import pojo.avro.LoanDecision;
import pojo.avro.LoanRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class LoanDecisionMakerTest {

    final Properties props = new Properties();

    @Before
    public void initProperties() {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put("schema.registry.url", "mock://test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    }

    @Test
    public void shouldMakeDecisionForExternalClient() {
        final SpecificAvroSerde<LoanRequest> requestSerde = Utils.getAvroSerde(props);
        final SpecificAvroSerde<LoanDecision> decisionSerde = Utils.getAvroSerde(props);
        final Serde<String> stringSerde = Serdes.String();

        try (final TopologyTestDriver testDriver = new TopologyTestDriver(LoanApprovalApp.getTopology(), props)) {

            final TestInputTopic<String, LoanRequest> requestsTopic = testDriver.createInputTopic(
                    Constants.LOAN_REQUESTS_TOPIC,
                    stringSerde.serializer(),
                    requestSerde.serializer(),
                    Instant.ofEpochMilli(0L),
                    Duration.ZERO);

            final TestOutputTopic<String, LoanDecision> decisionsTopic = testDriver.createOutputTopic(
                    Constants.LOAN_DECISIONS_TOPIC,
                    stringSerde.deserializer(),
                    decisionSerde.deserializer());

            // Mock records for the test
            LoanRequest ann = LoanRequest.newBuilder()
                    .setName("Anna").setSurname("Smith").setAmount(100).build();

            requestsTopic.pipeInput("1", ann);

            List<KeyValue<String, LoanDecision>> result = decisionsTopic.readKeyValuesToList();

            assertEquals("Incorrect number of decisions.", result.size(), 1);
            String source = result.get(0).value.getSource();
            assertEquals("Incorrect credit score source.", CreditScoreSource.EXTERNAL.name(), source);
        }
    }
    @Test
    public void shouldMakeDecisionForInternalClient() {
        final SpecificAvroSerde<LoanRequest> requestSerde = Utils.getAvroSerde(props);
        final SpecificAvroSerde<client_credit_score> clientSerde = Utils.getAvroSerde(props);
        final SpecificAvroSerde<LoanDecision> decisionSerde = Utils.getAvroSerde(props);
        final Serde<String> stringSerde = Serdes.String();

        try (final TopologyTestDriver testDriver = new TopologyTestDriver(LoanApprovalApp.getTopology(), props)) {

            final TestInputTopic<String, LoanRequest> requestsTopic = testDriver.createInputTopic(
                    Constants.LOAN_REQUESTS_TOPIC,
                    stringSerde.serializer(),
                    requestSerde.serializer(),
                    Instant.ofEpochMilli(0L),
                    Duration.ZERO);

            final TestInputTopic<String, client_credit_score> clientsTopic = testDriver.createInputTopic(
                    Constants.INTERNAL_CLIENTS_TOPIC,
                    stringSerde.serializer(),
                    clientSerde.serializer(),
                    Instant.ofEpochMilli(0L),
                    Duration.ZERO);

            final TestOutputTopic<String, LoanDecision> decisionsTopic = testDriver.createOutputTopic(
                    Constants.LOAN_DECISIONS_TOPIC,
                    stringSerde.deserializer(),
                    decisionSerde.deserializer());

            // Mock records for the test
            LoanRequest ann = LoanRequest.newBuilder()
                    .setName("Anna").setSurname("Smith").setAmount(100).build();
            client_credit_score creditScore = client_credit_score.newBuilder()
                    .setCreditScore(90).setFirstname("Anna").setLastname("Smith")
                    .setId(1).setTimestamp(Instant.now()).build();

            clientsTopic.pipeInput("1", creditScore);
            requestsTopic.pipeInput("1", ann);

            List<KeyValue<String, LoanDecision>> result = decisionsTopic.readKeyValuesToList();

            assertEquals("Incorrect number of decisions.", result.size(), 1);
            String source = result.get(0).value.getSource();
            assertEquals("Incorrect credit score source.", CreditScoreSource.INTERNAL.name(), source);
        }
    }
}
