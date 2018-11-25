package tikal.spotify.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import tikal.spotify.domain.UserGenre;

import java.io.IOException;
import java.util.Map;

public class UserGenreSerdes implements Serializer<UserGenre>, Deserializer<UserGenre> {
    private static UserGenreSerdes UserGenreSerdes = new UserGenreSerdes();
	private static Serde<UserGenre> serdes = Serdes.serdeFrom(UserGenreSerdes, UserGenreSerdes);
	private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

	@Override
	public UserGenre deserialize(String topic, byte[] data) {
		try {
			return mapper.readValue(data, UserGenre.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
    public byte[] serialize(String topic, UserGenre UserGenre) {
        try{
            return mapper.writeValueAsBytes(UserGenre);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }

    public static Serde<UserGenre> getSerdes(){
    	return serdes;
    }
}
