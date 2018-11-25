package tikal.spotify.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import tikal.spotify.domain.LocationWeather;

import java.io.IOException;
import java.util.Map;

public class LocationWeatherDeserializer implements Deserializer<LocationWeather> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public LocationWeather deserialize(String topic, byte[] bytes) {
        try {
            return mapper.readValue(bytes, LocationWeather.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
