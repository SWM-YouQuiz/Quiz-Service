package com.youquiz.quiz.adapter.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.youquiz.quiz.dto.event.CheckAnswerEvent
import com.youquiz.quiz.dto.event.LikeQuizEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun checkAnswer(event: CheckAnswerEvent) {
        kafkaTemplate.send("check-answer", objectMapper.writeValueAsString(event))
    }

    fun likeQuiz(event: LikeQuizEvent) {
        kafkaTemplate.send("like-quiz", objectMapper.writeValueAsString(event))
    }
}