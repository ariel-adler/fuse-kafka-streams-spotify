package tikal.spotify;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import tikal.spotify.domain.DomainGenerator;
import tikal.spotify.domain.LocationWeather;
import tikal.spotify.serdes.LocationWeatherSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class LocationWeatherProducer {
    public static String KAFKA_BROKERS = "localhost:9092";
    public static Integer MESSAGE_COUNT = 100;
    public static String TOPIC_NAME = "location_weathers";

    public static void main(String[] args) {
        LocationWeatherProducer locationWeatherProducer = new LocationWeatherProducer();
        locationWeatherProducer.runProducer();
    }

    private void runProducer() {
        Producer<String, LocationWeather> producer = createProducer();
        for (int index = 0; index < MESSAGE_COUNT; index++) {
            LocationWeather locationWeather = new LocationWeather();
            locationWeather.setWeatherType(DomainGenerator.weatherTypes.get(index % DomainGenerator.weatherTypes.size()));
            locationWeather.setCountry(DomainGenerator.countries.get(index % DomainGenerator.countries.size()));
            locationWeather.setCity(DomainGenerator.cities.get(index % DomainGenerator.cities.size()));
            ProducerRecord<String, LocationWeather> record = new ProducerRecord<>(TOPIC_NAME,
                    locationWeather.getCountry() + ";" + locationWeather.getCity(),
                    locationWeather);
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

    private Producer<String, LocationWeather> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LocationWeatherSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

}
