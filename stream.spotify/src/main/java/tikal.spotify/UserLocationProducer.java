package tikal.spotify;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import tikal.spotify.domain.DomainGenerator;
import tikal.spotify.domain.UserLocation;
import tikal.spotify.serdes.UserLocationSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class UserLocationProducer {
    public static String KAFKA_BROKERS = "localhost:9092";
    public static Integer MESSAGE_COUNT = 100;
    public static String TOPIC_NAME = "user_locations";

    public static void main(String[] args) {
        UserLocationProducer locationProducer = new UserLocationProducer();
        locationProducer.runProducer();
    }

    private void runProducer() {
        Producer<String, UserLocation> producer = createProducer();
        for (int index = 0; index < MESSAGE_COUNT; index++) {
            UserLocation userLocation = new UserLocation();
            userLocation.setEmail("test" + index + "@tikalk.com");
            userLocation.setCountry(DomainGenerator.countries.get(index % DomainGenerator.countries.size()));
            userLocation.setCity(DomainGenerator.cities.get(index % DomainGenerator.cities.size()));
            ProducerRecord<String, UserLocation> record = new ProducerRecord<>(TOPIC_NAME,
                    userLocation.getCountry() + ";" + userLocation.getCity(),
                        userLocation);
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

    private Producer<String, UserLocation> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserLocationSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}
