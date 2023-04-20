import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class.getSimpleName());

    public static <T extends SpecificRecord> SpecificAvroSerde<T> getAvroSerde(Properties context) {
        Map<String, String> map = context.entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())
        );

        final SpecificAvroSerde<T> serde = new SpecificAvroSerde<>();
        serde.configure(map, false);
        return serde;
    }

    public static Properties readProperties(String file) {

        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(file)) {
            Reader reader = new InputStreamReader(inputStream);
            properties.load(reader);

        } catch (IOException e) {
            log.error("Can't read configuration file.");
            exit(-1);
        }

        return properties;
    }
}
