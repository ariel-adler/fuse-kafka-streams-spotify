package tikal.spotify;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import tikal.spotify.domain.LocationWeather;
import tikal.spotify.domain.UserLocation;
import tikal.spotify.domain.UserWeather;
import tikal.spotify.serdes.UserWeatherSerdes;
import tikal.spotify.serdes.LocationWeatherSerializer;
import tikal.spotify.serdes.LocationWeatherDeserializer;
import tikal.spotify.serdes.UserLocationSerializer;
import tikal.spotify.serdes.UserLocationDeserializer;


import java.util.concurrent.TimeUnit;

public class UserWeatherProducer {
    public static String KAFKA_BROKERS = "localhost:9092";
    public static String LOCATION_WEATHER_TOPIC_NAME = "location_weathers";
    public static String USER_LOCATION_TOPIC_NAME = "user_locations";
    public static String OUT_TOPIC_NAME = "user_weather";

    public void joinUserWeather() {
        final StreamsBuilder builder = new StreamsBuilder();

        Serde<UserLocation> userLocationSerde = Serdes.serdeFrom(new UserLocationSerializer(), new UserLocationDeserializer());
        Serde<LocationWeather> locationWeatherSerde = Serdes.serdeFrom(new LocationWeatherSerializer(), new LocationWeatherDeserializer());

        KStream<String, UserLocation> userLocationStream = builder.stream(USER_LOCATION_TOPIC_NAME, Consumed.with(
                Serdes.String(),
                userLocationSerde
        ));
        KStream<String, LocationWeather> locationWeatherStream = builder.stream(LOCATION_WEATHER_TOPIC_NAME, Consumed.with(
                Serdes.String(),
                locationWeatherSerde
        ));

        KStream<String, UserWeather> join = userLocationStream.join(locationWeatherStream, (userLocation, locationWeather) ->
                        new UserWeather(userLocation.getEmail(), locationWeather.getWeatherType()),
                JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
                Joined.with(
                        Serdes.String(),
                        userLocationSerde,
                        locationWeatherSerde
                )
        );

        join.to("output", Produced.with(
                Serdes.String(),
                UserWeatherSerdes.getSerdes()
        ));

//        join.map((key, value)-> )
    }

}
