package com.quizit.quiz.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfiguration {
    @Bean
    fun reactiveKafkaProducerTemplate(
        properties: KafkaProperties, objectMapper: ObjectMapper
    ): ReactiveKafkaProducerTemplate<String, Any> =
        ReactiveKafkaProducerTemplate(
            SenderOptions.create<String, Any>(properties.buildProducerProperties())
                .withValueSerializer(JsonSerializer(objectMapper))
        )
}