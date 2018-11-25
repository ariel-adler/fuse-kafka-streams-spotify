package tikal.spotify.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import tikal.spotify.domain.UserWeather;

import java.io.IOException;
import java.util.Map;

public class UserWeatherSerdes implements Serializer<UserWeather>, Deserializer<UserWeather> {
    private static UserWeatherSerdes userWeatherSerdes = new UserWeatherSerdes();
	private ObjectMapper mapper = new ObjectMapper();
	private static Serde<UserWeather> serdes = Serdes.serdeFrom(userWeatherSerdes, userWeatherSerdes);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

	@Override
	public UserWeather deserialize(String topic, byte[] data) {
		try {
			return mapper.readValue(data, UserWeather.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
    public byte[] serialize(String topic, UserWeather userWeather) {
        try{
            return mapper.writeValueAsBytes(userWeather);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }

    public static Serde<UserWeather> getSerdes(){
    	return serdes;
    }
}
