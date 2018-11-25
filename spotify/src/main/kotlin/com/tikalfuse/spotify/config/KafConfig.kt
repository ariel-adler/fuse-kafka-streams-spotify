package com.tikalfuse.spotify.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.tikalfuse.spotify.entities.User
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig constructor(@Value("\${kafka.bootstrap.address}") private val kafkaAddress: String) {

    @Bean
    fun producerFactory(): ProducerFactory<String, User> {
        val propConfig = HashMap<String, Any>()
        propConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaAddress
        propConfig[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.canonicalName
        propConfig[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.canonicalName
        return DefaultKafkaProducerFactory<String, User>(propConfig)

        return DefaultKafkaProducerFactory<String, User>(propConfig,
                StringSerializer(),
                JsonSerializer(ObjectMapper()))
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, User> = KafkaTemplate(producerFactory())
}