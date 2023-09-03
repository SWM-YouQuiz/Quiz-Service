package com.quizit.quiz.adapter.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.quiz.dto.event.CheckAnswerEvent
import com.quizit.quiz.dto.event.MarkQuizEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class QuizProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun checkAnswer(event: CheckAnswerEvent) {
        kafkaTemplate.send("check-answer", objectMapper.writeValueAsString(event))
    }

    fun markQuiz(event: MarkQuizEvent) {
        kafkaTemplate.send("mark-quiz", objectMapper.writeValueAsString(event))
    }
}