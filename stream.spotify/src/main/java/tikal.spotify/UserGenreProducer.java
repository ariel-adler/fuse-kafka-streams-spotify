package tikal.spotify;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import tikal.spotify.domain.UserGenre;
import tikal.spotify.serdes.UserGenreSerdes;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class UserGenreProducer {

	public static String KAFKA_BROKERS = "localhost:9092";
	public static Integer MESSAGE_COUNT = 100;
	public static String TOPIC_NAME = "user_genres";

	public static void main(String[] args) {
		UserGenreProducer songsProducer = new UserGenreProducer();
		songsProducer.runProducer();
	}

	private void runProducer() {
		Producer<String, UserGenre> producer = createProducer();
		for (int index = 0; index < MESSAGE_COUNT; index++) {
			ProducerRecord<String, UserGenre> record = new ProducerRecord<>(TOPIC_NAME,
					"test" + index + "@tikalk.com",
					UserGenre.builder().email("test" + index + "@tikalk.com").trackId("abc-" + index*2).build());

			try {
				RecordMetadata metadata = producer.send(record).get();
				System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
						+ " with offset " + metadata.offset());
			}
			catch (ExecutionException | InterruptedException e) {
				System.out.println("Error in sending record");
				System.out.println(e);
			}
		}
	}


	private Producer<String, UserGenre> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserGenreSerdes.class.getName());
		//		props.put(ProducerConfig.ACKS_CONFIG, "all");
		//props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
		return new KafkaProducer<>(props);
	}
}
