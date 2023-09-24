package com.quizit.quiz.repository

import com.quizit.quiz.config.RedisTestConfiguration
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createQuiz
import com.quizit.quiz.fixture.objectMapper
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ContextConfiguration(classes = [RedisTestConfiguration::class])
class QuizCacheRepositoryTest : ExpectSpec() {
    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    private val quizRepository = mockk<QuizRepository>()
        .apply {
            every { findById(any<String>()) } returns Mono.empty()
        }

    private val quizCacheRepository by lazy {
        QuizCacheRepository(
            quizRepository = quizRepository,
            redisTemplate = redisTemplate,
            objectMapper = objectMapper
        )
    }

    override suspend fun beforeContainer(testCase: TestCase) {
        redisTemplate.execute {
            it.serverCommands()
                .flushAll()
        }.subscribe()
    }

    init {
        context("캐시 조회") {
            val quiz = createQuiz()
                .also {
                    quizCacheRepository.save(it)
                        .subscribe()
                }

            expect("캐시를 조회한다.") {
                val result = StepVerifier.create(quizCacheRepository.findById(ID))

                result.expectSubscription()
                    .assertNext { it shouldBeEqualToComparingFields quiz }
                    .verifyComplete()
            }
        }

        context("캐시 삭제") {
            createQuiz()
                .also {
                    quizCacheRepository.save(it)
                        .subscribe()
                }

            expect("특정 유저의 리프레쉬 토큰을 삭제한다.") {
                val result = StepVerifier.create(quizCacheRepository.deleteById(ID))

                result.expectSubscription()
                    .assertNext {
                        quizCacheRepository.findById(ID)
                            .subscribe { it.shouldBeNull() }
                    }
                    .verifyComplete()
            }
        }
    }
}