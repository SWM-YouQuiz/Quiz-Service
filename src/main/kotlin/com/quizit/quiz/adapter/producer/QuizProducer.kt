package com.quizit.quiz.adapter.producer

import com.quizit.quiz.dto.event.CheckAnswerEvent
import com.quizit.quiz.dto.event.DeleteQuizEvent
import com.quizit.quiz.dto.event.MarkQuizEvent
import com.quizit.quiz.global.config.producerLogging
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

@Component
class QuizProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
) {
    fun deleteQuiz(event: DeleteQuizEvent): Mono<SenderResult<Void>> =
        kafkaTemplate.send("delete-quiz", event)
            .doOnNext { producerLogging(event) }

    fun markQuiz(event: MarkQuizEvent): Mono<SenderResult<Void>> =
        kafkaTemplate.send("mark-quiz", event)
            .doOnNext { producerLogging(event) }
}