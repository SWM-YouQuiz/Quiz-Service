package com.quizit.quiz.adapter.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.quiz.dto.event.CheckAnswerEvent
import com.quizit.quiz.dto.event.MarkQuizEvent
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class QuizProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {
    fun checkAnswer(event: CheckAnswerEvent): Mono<Void> =
        kafkaTemplate.send("check-answer", objectMapper.writeValueAsString(event))
            .then()

    fun markQuiz(event: MarkQuizEvent): Mono<Void> =
        kafkaTemplate.send("mark-quiz", objectMapper.writeValueAsString(event))
            .then()
}