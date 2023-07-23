package com.youquiz.quiz.service

import com.youquiz.quiz.adapter.client.UserClient
import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.dto.*
import com.youquiz.quiz.fixture.*
import com.youquiz.quiz.repository.QuizRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class QuizServiceTest : BehaviorSpec() {
    private val quizRepository = mockk<QuizRepository>()

    private val userClient = mockk<UserClient>()

    private val userProducer = mockk<UserProducer>().apply {
        coEvery { correctAnswer(any()) } just Runs
        coEvery { incorrectAnswer(any()) } just Runs
    }

    private val quizService = QuizService(
        quizRepository = quizRepository,
        userClient = userClient,
        userProducer = userProducer
    )

    init {
        Given("챕터와 각각의 챕터에 속하는 퀴즈들이 존재하는 경우") {
            val quizzes = List(3) { createQuiz() }.apply {
                asFlow().let {
                    coEvery { quizRepository.findAllByChapterId(any()) } returns it
                    coEvery { quizRepository.findAllByIdIn(any()) } returns it
                }
            }

            When("유저가 챕터를 들어가면") {
                val quizResponses = quizService.findAllByChapterId(CHAPTER_ID).toList()

                Then("해당 챕터에 속하는 퀴즈들이 주어진다.") {
                    quizResponses shouldContainExactly quizzes.map { QuizResponse(it) }
                }
            }
        }

        Given("유저가 저장한 퀴즈가 존재하는 경우") {
            val markedQuizzes = List(3) { createQuiz() }.apply {
                coEvery { quizRepository.findAllByIdIn(any()) } returns asFlow()
            }

            When("유저가 본인이 저장한 퀴즈 보관함에 들어가면") {
                val quizResponses = quizService.findAllMarkedQuiz(
                    FindAllMarkedQuizRequest(
                        quizIds = markedQuizzes.map { it.id!! }
                    )).toList()

                Then("저장한 퀴즈들이 주어진다.") {
                    quizResponses shouldContainExactly markedQuizzes.map { QuizResponse(it) }
                }
            }
        }

        Given("유저가 작성한 퀴즈가 존재하는 경우") {
            val writtenQuizzes = List(3) { createQuiz() }.apply {
                coEvery { quizRepository.findAllByWriterId(any()) } returns asFlow()
            }

            When("유저가 본인이 작성한 퀴즈 보관함에 들어가면") {
                val quizResponses = quizService.findAllByWriterId(ID).toList()

                Then("본인이 작성한 퀴즈들이 주어진다.") {
                    quizResponses shouldContainExactly writtenQuizzes.map { QuizResponse(it) }
                }
            }
        }

        Given("유저가 퀴즈를 푼 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.findById(any()) } returns it
                coEvery { quizRepository.save(any()) } returns it
            }

            When("옳은 답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = CheckAnswerRequest(
                        quizId = OBJECT_ID,
                        answer = quiz.answer
                    )
                )

                Then("정답으로 처리된다.") {
                    checkAnswerResponse.isAnswer shouldBe true
                }
            }

            When("틀린 답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = CheckAnswerRequest(
                        quizId = OBJECT_ID,
                        answer = quiz.answer + 1
                    )
                )

                Then("오답으로 처리된다.") {
                    checkAnswerResponse.isAnswer shouldBe false
                }
            }
        }

        Given("유저가 퀴즈를 작성하는 중인 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { userClient.findById(any()) } returns FindUserByIdResponse(
                    id = ID,
                    nickname = NICKNAME
                )

            }

            When("유저가 퀴즈를 제출하면") {
                val quizResponse = quizService.createQuiz(
                    userId = ID,
                    request = quiz.run {
                        CreateQuizRequest(
                            question = question,
                            answer = answer,
                            solution = solution,
                            writerId = ID,
                            chapterId = chapterId
                        )
                    }
                )

                Then("퀴즈가 생성된다.") {
                    quizResponse shouldBeEqualToComparingFields QuizResponse(quiz)
                }
            }
        }
    }
}