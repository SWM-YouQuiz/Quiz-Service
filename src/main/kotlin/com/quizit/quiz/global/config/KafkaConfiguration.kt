package com.quizit.quiz.global.config

import com.quizit.quiz.dto.event.DeleteUserEvent
import com.quizit.quiz.global.util.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfiguration(
    private val properties: KafkaProperties
) {
    @Bean
    fun reactiveKafkaProducerTemplate(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, Any> =
        ReactiveKafkaProducerTemplate(
            SenderOptions.create<String, Any>(properties.buildProducerProperties())
                .withValueSerializer(JsonSerializer())
        )

    @Bean
    fun deleteUserConsumer(): ReactiveKafkaConsumerTemplate<String, DeleteUserEvent> =
        ReactiveKafkaConsumerTemplate(receiverOptions("delete-user"))

    private inline fun <reified T> receiverOptions(topic: String): ReceiverOptions<String, T> =
        ReceiverOptions.create<String, T>(
            properties.run {
                consumer.groupId = "$topic-group"
                buildConsumerProperties()
            })
            .subscription(listOf(topic))
            .withValueDeserializer(JsonDeserializer<T>(T::class.java, false))
}

fun producerLogging(event: Any) {
    logger.info { "Successfully produced ${event::class.simpleName}: $event" }
}

fun consumerLogging(consumerRecord: ConsumerRecord<String, out Any>) {
    with(consumerRecord.value()) {
        logger.info { "Successfully consumed ${this::class.simpleName}: $this" }
    }
}