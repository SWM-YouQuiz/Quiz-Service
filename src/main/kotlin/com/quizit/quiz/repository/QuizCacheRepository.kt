package com.quizit.quiz.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.quiz.domain.Quiz
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class QuizCacheRepository(
    private val quizRepository: QuizRepository,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    suspend fun findById(id: String): Quiz? =
        redisTemplate.opsForValue()
            .get(getKey(id))
            .awaitSingleOrNull()
            ?.let { objectMapper.readValue(it, Quiz::class.java) } ?: quizRepository.findById(id)

    suspend fun save(quiz: Quiz): Boolean =
        redisTemplate.opsForValue()
            .set(getKey(quiz.id!!), objectMapper.writeValueAsString(quiz), Duration.ofMinutes(60))
            .awaitSingle()

    suspend fun deleteById(id: String): Boolean =
        redisTemplate.opsForValue()
            .delete(getKey(id))
            .awaitSingle()

    private fun getKey(id: String): String = "quiz:$id"
}