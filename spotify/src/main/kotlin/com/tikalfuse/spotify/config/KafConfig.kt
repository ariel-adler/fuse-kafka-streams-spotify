package com.tikalfuse.spotify.config

import com.tikalfuse.spotify.entities.Genre
import com.tikalfuse.spotify.entities.Track
import com.tikalfuse.spotify.entities.User
import com.tikalfuse.spotify.entities.UserToTrack
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.concurrent.TimeUnit

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaProducerConfig {

    private val kafkaAddress: String = "localhost:9092"

    @Value("\${delivery-stats.stream.threads:1}")
    private lateinit var threads: Integer

    @Value("\${delivery-stats.kafka.replication-factor:1}")
    private lateinit var replicationFactor: Integer

    @Value("\${messaging.kafka-dp.brokers.url:localhost:9092}")
    private lateinit var brokersUrl: String

    @Value("\${user-to-track-id.join.window-time.millis}")
    private lateinit var userToTrackJoinWindowTime: Integer

    // Topics
    @Value("\${topic.user-profile}")
    private lateinit var userTopic: String

    @Value("\${topic.tracks}")
    private lateinit var tracksTopic: String

    @Value("\${topic.user-tracks:user_tracks_by_genre}")
    private lateinit var userTracksTopic: String

    @Value("\${topic.user-genres}")
    private lateinit var userGenreTopic: String

    @Value("\${topic.genre-trackId}")
    private lateinit var genreTrackIdTopic: String


    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamsConfigs(): KafkaStreamsConfiguration {
        val props = HashMap<String, Any?>()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "userTracks"
        setDefaults(props)
        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun streamUserTracksByGenre(streamBuilder: StreamsBuilder): KStream<String, UserToTrack> {
        val userGenresStream: KStream<Genre, String> =
                streamBuilder.stream<String, User>(userTopic,
                        Consumed.with(Serdes.StringSerde(), JsonSerde(User::class.java)))
                .selectKey { key, value -> value.email }
                .flatMapValues { it -> it.genres.asIterable() }
                .map { key, value -> KeyValue(value, key) }
                .through(userGenreTopic, Produced.with(JsonSerde(Genre::class.java), Serdes.StringSerde()))

        val tracksStream: KStream<Genre, String> =
                streamBuilder.stream<String, Track>(tracksTopic,
                        Consumed.with(Serdes.StringSerde(), JsonSerde(Track::class.java)))
                .selectKey { key, value -> value.genre }
                .mapValues { it -> it.id.toString() }
                .through(genreTrackIdTopic, Produced.with(JsonSerde(Genre::class.java), Serdes.StringSerde()))


        val userIdToTrack: KStream<String, UserToTrack> =
                userGenresStream.join<String, UserToTrack>(
                        tracksStream,
                        UserToTrackJoiner(),
                        JoinWindows.of(TimeUnit.MILLISECONDS.toMillis(userToTrackJoinWindowTime.toLong())))
                    .selectKey { key, value -> "${value.userId}.${value.trackId}" }

        userIdToTrack.to(userTracksTopic, Produced.with(Serdes.StringSerde(), JsonSerde(UserToTrack::class.java)))


        return userIdToTrack
    }


    @Bean
    fun producerFactory(): ProducerFactory<String, User> {
        val propConfig = HashMap<String, Any>()
        propConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaAddress
        propConfig[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.canonicalName
        propConfig[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.canonicalName

        return DefaultKafkaProducerFactory<String, User>(propConfig)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, User> = KafkaTemplate(producerFactory())

    /*@Bean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    fun kStreamsConfigs(): StreamsConfig {
        val config = HashMap<String, Any?>()
        config[StreamsConfig.APPLICATION_ID_CONFIG] = "default"
        setDefaults(config)

        return StreamsConfig(config)
    }*/

    /*@Bean
    fun myKStreamBuilder(streamsConfig: KafkaStreamsConfiguration): StreamsBuilderFactoryBean {
        return StreamsBuilderFactoryBean(streamsConfig)
    }*/

    /*@Bean("ooo")
    @Primary
    fun streamBuilderFactoryBean(): StreamsBuilderFactoryBean {
        val config = HashMap<String, Any?>()
        setDefaults(config)

        config[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE
        config[StreamsConfig.APPLICATION_ID_CONFIG] = "app1"
        config[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 30000
        config[StreamsConfig.NUM_STREAM_THREADS_CONFIG] = threads
        config[StreamsConfig.REPLICATION_FACTOR_CONFIG] = replicationFactor



        return StreamsBuilderFactoryBean(KafkaStreamsConfiguration(config))

    }*/

    fun setDefaults(config: MutableMap<String, Any?>) {
        config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = brokersUrl
        config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = JsonSerde::class.java
        config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = JsonSerde::class.java
        config[JsonDeserializer.KEY_DEFAULT_TYPE] = Genre::class.java

        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        config[StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG] = FailOnInvalidTimestamp::class.java
    }

    class UserToTrackJoiner : ValueJoiner<String, String, UserToTrack> {
        override fun apply(value1: String, value2: String): UserToTrack {
            return UserToTrack(value1, value2)
        }
    }
}