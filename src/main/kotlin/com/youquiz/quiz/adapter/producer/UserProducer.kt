package com.youquiz.quiz.adapter.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun correctAnswer(userId: Long) {
        kafkaTemplate.send("correctAnswer", userId.toString())
    }

    fun incorrectAnswer(userId: Long) {
        kafkaTemplate.send("incorrectAnswer", userId.toString())
    }
}