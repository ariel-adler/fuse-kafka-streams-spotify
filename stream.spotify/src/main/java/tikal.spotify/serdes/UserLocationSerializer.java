package tikal.spotify.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import tikal.spotify.domain.UserLocation;

import java.util.Map;

public class UserLocationSerializer implements Serializer<UserLocation> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, UserLocation userLocation) {
        try{
            return mapper.writeValueAsBytes(userLocation);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
