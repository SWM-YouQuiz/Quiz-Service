package com.quizit.quiz.adapter.consumer

import com.quizit.quiz.dto.event.DeleteUserEvent
import com.quizit.quiz.global.aop.annotation.Consumer
import com.quizit.quiz.global.config.consumerLogging
import com.quizit.quiz.repository.QuizRepository
import jakarta.annotation.PostConstruct
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate

@Consumer
class QuizConsumer(
    private val deleteUserConsumer: ReactiveKafkaConsumerTemplate<String, DeleteUserEvent>,
    private val quizRepository: QuizRepository
) {
    @PostConstruct
    fun deleteUser() {
        deleteUserConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    quizRepository.findAll()
                        .map {
                            it.apply {
                                markedUserIds.remove(userId)
                                likedUserIds.remove(userId)
                            }
                        }
                        .let { quizRepository.saveAll(it) }
                        .subscribe()
                }
            }
            .doOnNext { consumerLogging(it) }
            .subscribe()
    }
}