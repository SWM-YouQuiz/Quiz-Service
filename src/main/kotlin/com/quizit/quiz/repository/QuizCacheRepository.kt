package com.quizit.quiz.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.quiz.domain.Quiz
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class QuizCacheRepository(
    private val quizRepository: QuizRepository,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun findById(id: String): Mono<Quiz> =
        redisTemplate.opsForValue()
            .get(getKey(id))
            .map { objectMapper.readValue(it, Quiz::class.java) }
            .switchIfEmpty(Mono.defer { quizRepository.findById(id) })

    fun save(quiz: Quiz): Mono<Boolean> =
        redisTemplate.opsForValue()
            .set(getKey(quiz.id!!), objectMapper.writeValueAsString(quiz), Duration.ofMinutes(60))

    fun deleteById(id: String): Mono<Boolean> =
        redisTemplate.opsForValue()
            .delete(getKey(id))

    private fun getKey(id: String): String = "quiz:$id"
}