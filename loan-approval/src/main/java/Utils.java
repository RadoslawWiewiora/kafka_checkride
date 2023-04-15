import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Utils {

    public static <T extends SpecificRecord> SpecificAvroSerde<T> getAvroSerde(Properties context) {
        Map<String, String> map = context.entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())
        );

        final SpecificAvroSerde<T> serde = new SpecificAvroSerde<>();
        serde.configure(map, false);
        return serde;
    }
}
