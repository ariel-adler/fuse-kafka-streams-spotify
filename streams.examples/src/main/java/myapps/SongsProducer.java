package myapps;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SongsProducer {

	public static String KAFKA_BROKERS = "localhost:9092";
	public static Integer MESSAGE_COUNT = 100;
	public static String CLIENT_ID = "streams-wordcount";
	public static String TOPIC_NAME = "songs";
//	public static String TOPIC_NAME = "streams-plaintext-input";
	public static String GROUP_ID_CONFIG = "consumerGroup1";
	public static Integer MAX_NO_MESSAGE_FOUND_COUNT = 100;
	public static String OFFSET_RESET_LATEST = "latest";
	public static String OFFSET_RESET_EARLIER = "earliest";
	public static Integer MAX_POLL_RECORDS = 1;

	public static void main(String[] args) {
		boolean consumer = args != null && args.length > 0 && args[0].equals("consumer");
		if (consumer) {
			runConsumer();
		}
		else {
			runProducer();
		}
	}

	static void runConsumer() {
		Consumer<Long, String> consumer = createConsumer();
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
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

	static void runProducer() {
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

	public static Consumer<Long, String> createConsumer() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET_EARLIER);
		Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(TOPIC_NAME));
		return consumer;
	}

	public static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
		return new KafkaProducer<>(props);
	}
}
