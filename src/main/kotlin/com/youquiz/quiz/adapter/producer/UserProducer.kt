package com.youquiz.quiz.adapter.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.youquiz.quiz.event.CorrectAnswerEvent
import com.youquiz.quiz.event.IncorrectAnswerEvent
import com.youquiz.quiz.event.LikeEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun correctAnswer(correctAnswerEvent: CorrectAnswerEvent) {
        kafkaTemplate.send("correct-answer", objectMapper.writeValueAsString(correctAnswerEvent))
    }

    fun incorrectAnswer(incorrectAnswerEvent: IncorrectAnswerEvent) {
        kafkaTemplate.send("incorrect-answer", objectMapper.writeValueAsString(incorrectAnswerEvent))
    }

    fun likeQuiz(event: LikeEvent) {
        kafkaTemplate.send("like-quiz", objectMapper.writeValueAsString(event))
    }
}