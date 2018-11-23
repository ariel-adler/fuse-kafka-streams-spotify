package tikal.spotify;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SongsProducer {

	public static String KAFKA_BROKERS = "localhost:9092";
	public static Integer MESSAGE_COUNT = 100;
	public static String TOPIC_NAME = "songs";

	public static void main(String[] args) {
		SongsProducer songsProducer = new SongsProducer();
		songsProducer.runProducer();
	}

	private void runProducer() {
		Producer<Long, String> producer = createProducer();
		for (int index = 0; index < MESSAGE_COUNT; index++) {
			ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(TOPIC_NAME,
					"This is record " + index);
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


	private Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//		props.put(ProducerConfig.ACKS_CONFIG, "all");
		//props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
		return new KafkaProducer<>(props);
	}
}
