package tikal.spotify.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import tikal.spotify.domain.Recommendation;

import java.io.IOException;
import java.util.Map;

public class RecommendationSerdes implements Serializer<Recommendation>, Deserializer<Recommendation> {
    private static RecommendationSerdes RecommendationSerdes = new RecommendationSerdes();
	private ObjectMapper mapper = new ObjectMapper();
	private static Serde<Recommendation> serdes = Serdes.serdeFrom(RecommendationSerdes, RecommendationSerdes);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

	@Override
	public Recommendation deserialize(String topic, byte[] data) {
		try {
			return mapper.readValue(data, Recommendation.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
    public byte[] serialize(String topic, Recommendation Recommendation) {
        try{
            return mapper.writeValueAsBytes(Recommendation);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }

    public static Serde<Recommendation> getSerdes(){
    	return serdes;
    }
}
