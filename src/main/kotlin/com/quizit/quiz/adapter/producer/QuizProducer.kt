package com.quizit.quiz.adapter.producer

import com.quizit.quiz.dto.event.CheckAnswerEvent
import com.quizit.quiz.dto.event.DeleteQuizEvent
import com.quizit.quiz.dto.event.MarkQuizEvent
import com.quizit.quiz.global.annotation.Producer
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono

@Producer
class QuizProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
) {
    fun deleteQuiz(event: DeleteQuizEvent): Mono<Void> =
        kafkaTemplate.send("delete-quiz", event)
            .then()

    fun checkAnswer(event: CheckAnswerEvent): Mono<Void> =
        kafkaTemplate.send("check-answer", event)
            .then()

    fun markQuiz(event: MarkQuizEvent): Mono<Void> =
        kafkaTemplate.send("mark-quiz", event)
            .then()
}