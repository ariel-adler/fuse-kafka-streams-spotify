/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tikal.spotify;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import tikal.spotify.domain.Recommendation;
import tikal.spotify.domain.UserGenre;
import tikal.spotify.domain.UserWeather;
import tikal.spotify.serdes.RecommendationSerdes;
import tikal.spotify.serdes.UserGenreSerdes;
import tikal.spotify.serdes.UserWeatherSerdes;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code merge each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class WeatherGenreStream {

	static final String USER_GENRES_TOPIC = "user_genres";
	static final String USER_WEATHER_TOPIC = "user_weather";

	public static void main(String[] args) throws Exception {
		WeatherGenreStream songsFilter = new WeatherGenreStream();
		songsFilter.merge();
	}

	private void merge() throws Exception {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "weather-genre");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, SongsProducer.KAFKA_BROKERS);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		final StreamsBuilder builder = new StreamsBuilder();

		KStream<String, UserWeather> right = builder.stream(USER_WEATHER_TOPIC, Consumed.with(
				Serdes.String(),
				UserWeatherSerdes.getSerdes()
		));
		KStream<String, UserGenre> left = builder.stream(USER_GENRES_TOPIC, Consumed.with(
				Serdes.String(),
				UserGenreSerdes.getSerdes()
		));
		KStream<String, Recommendation> stream = right.join(left,
				(userWeather, userGenre) ->
						Recommendation.builder().email(userWeather.getEmail()).trackId(userGenre.getTrackId()).build(),
				JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
				Joined.with(
						Serdes.String(),
						UserWeatherSerdes.getSerdes(),
						UserGenreSerdes.getSerdes()
				)
		);
		stream.to("recommendations", Produced.with(
				Serdes.String(), RecommendationSerdes.getSerdes()

		));

		final Topology topology = builder.build();
		final KafkaStreams streams = new KafkaStreams(topology, props);
		final CountDownLatch latch = new CountDownLatch(1);

		// attach shutdown handler to catch control-c
		Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
			@Override
			public void run() {
				streams.close();
				latch.countDown();
			}
		});

		try {
			streams.start();
			latch.await();
		}
		catch (Throwable e) {
			System.exit(1);
		}
		System.exit(0);
	}

	private boolean isFive(String s) {
		return s.endsWith("5") || s.endsWith("0");
	}
}
