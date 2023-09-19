package com.quizit.quiz.repository

import com.quizit.quiz.config.RedisTestConfiguration
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createQuiz
import com.quizit.quiz.fixture.objectMapper
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [RedisTestConfiguration::class])
class QuizCacheRepositoryTest : ExpectSpec() {
    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    private val quizRepository = mockk<QuizRepository>().apply {
        coEvery { findById(any()) } returns null
    }

    private val quizCacheRepository by lazy {
        QuizCacheRepository(
            quizRepository = quizRepository,
            redisTemplate = redisTemplate,
            objectMapper = objectMapper
        )
    }

    override suspend fun beforeContainer(testCase: TestCase) {
        redisTemplate.execute { it.serverCommands().flushAll() }.awaitSingle()
    }

    init {
        context("캐시 조회") {
            val quiz = createQuiz().also { quizCacheRepository.save(it) }

            expect("캐시를 조회한다.") {
                quizCacheRepository.findById(ID)!! shouldBeEqualToComparingFields quiz
            }
        }

        context("캐시 삭제") {
            coEvery { quizRepository.findById(any()) } returns null
            createQuiz().let { quizCacheRepository.save(it) }

            expect("특정 유저의 리프레쉬 토큰을 삭제한다.") {
                quizCacheRepository.deleteById(ID)

                quizCacheRepository.findById(ID).shouldBeNull()
            }
        }
    }
}