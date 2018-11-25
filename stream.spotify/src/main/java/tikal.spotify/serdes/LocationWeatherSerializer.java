package tikal.spotify.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import tikal.spotify.domain.LocationWeather;

import java.util.Map;

public class LocationWeatherSerializer implements Serializer<LocationWeather> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, LocationWeather locationWeather) {
        try{
            return mapper.writeValueAsBytes(locationWeather);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
