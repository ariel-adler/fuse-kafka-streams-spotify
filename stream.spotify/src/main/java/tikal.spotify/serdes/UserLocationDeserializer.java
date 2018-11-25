package tikal.spotify.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import tikal.spotify.domain.UserLocation;

import java.io.IOException;
import java.util.Map;

public class UserLocationDeserializer implements Deserializer {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public UserLocation deserialize(String topic, byte[] bytes) {
        try {
            return mapper.readValue(bytes, UserLocation.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
