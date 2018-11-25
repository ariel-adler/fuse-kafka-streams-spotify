package tikal.spotify;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import tikal.spotify.domain.UserWeather;
import tikal.spotify.serdes.UserWeatherSerdes;

import java.util.Collections;
import java.util.Properties;

public class UserWeatherConsumer {

	public static String KAFKA_BROKERS = "localhost:9092";
	public static String TOPIC_NAME = "user_weather";
	public static String GROUP_ID_CONFIG = "consumerGroup1";
	public static Integer MAX_NO_MESSAGE_FOUND_COUNT = 100000;
	public static String OFFSET_RESET_LATEST = "latest";
	public static String OFFSET_RESET_EARLIER = "earliest";
	public static Integer MAX_POLL_RECORDS = 1;

	public static void main(String[] args) {
		UserWeatherConsumer songsConsumer = new UserWeatherConsumer();
		songsConsumer.runConsumer();
	}

	private void runConsumer() {
		Consumer<String, UserWeather> consumer = createConsumer();
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<String, UserWeather> consumerRecords = consumer.poll(1000);
			// 1000 is the time in milliseconds consumer will wait if no record is found at broker.
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > MAX_NO_MESSAGE_FOUND_COUNT)
				// If no message found count is reached to threshold exit loop.
				{
					break;
				}
				else {
					continue;
				}
			}
			//print each record.
			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}


	private Consumer<String, UserWeather> createConsumer() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserWeatherSerdes.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET_EARLIER);
		Consumer<String, UserWeather> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(TOPIC_NAME));
		return consumer;
	}
}
