package com.quizit.quiz.adapter.consumer

import com.quizit.quiz.dto.event.DeleteUserEvent
import com.quizit.quiz.global.annotation.Consumer
import com.quizit.quiz.repository.QuizRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux

@Consumer
class UserConsumer(
    private val deleteUserConsumer: ReactiveKafkaConsumerTemplate<String, DeleteUserEvent>,
    private val quizRepository: QuizRepository
) {
    @EventListener(ApplicationStartedEvent::class)
    fun deleteUser(): Flux<ConsumerRecord<String, DeleteUserEvent>> =
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
}