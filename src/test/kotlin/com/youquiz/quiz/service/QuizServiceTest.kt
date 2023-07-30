package com.youquiz.quiz.service

import com.youquiz.quiz.adapter.client.UserClient
import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.fixture.*
import com.youquiz.quiz.repository.QuizRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.mockk.*
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

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

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

        Given("유저가 좋아요한 퀴즈가 존재하는 경우") {
            val markedQuizzes = List(3) { createQuiz() }.apply {
                coEvery { quizRepository.findAllByIdIn(any()) } returns asFlow()
            }

            coEvery { userClient.findById(any()) } returns createFindUserByIdResponse()

            When("유저가 본인이 좋아요한 퀴즈 보관함에 들어가면") {
                val quizResponses = quizService.findAllLikedQuiz(userId = ID).toList()

                Then("유저가 좋아요한 퀴즈들이 주어진다.") {
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

            coEvery { userClient.findById(any()) } returns createFindUserByIdResponse()

            When("옳은 답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = createCheckAnswerRequest()
                )

                Then("정답으로 처리되어 정답률이 변경된다.") {
                    checkAnswerResponse.isAnswer shouldBe true
                    verify { userProducer.correctAnswer(any()) }
                }
            }

            When("틀린 답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = createCheckAnswerRequest(answer = -1)
                )

                Then("오답으로 처리되어 정답률이 변경된다.") {
                    checkAnswerResponse.isAnswer shouldBe false
                    verify { userProducer.incorrectAnswer(any()) }
                }
            }
        }

        Given("유저가 이미 푼 퀴즈가 존재하는 경우") {
            val quiz = createQuiz(id = "quiz_1").also {
                coEvery { quizRepository.findById(any()) } returns it
                coEvery { quizRepository.save(any()) } returns it
            }

            coEvery { userClient.findById(any()) } returns createFindUserByIdResponse()

            When("해당 퀴즈를 풀고 정답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = createCheckAnswerRequest()
                )


                Then("채점만 되고 정답률은 변경되지 않는다.") {
                    checkAnswerResponse.isAnswer shouldBe true
                    verify(exactly = 0) { userProducer.correctAnswer(any()) }
                }
            }

            When("해당 퀴즈를 풀고 오답을 제출하면") {
                val checkAnswerResponse = quizService.checkAnswer(
                    userId = ID,
                    request = createCheckAnswerRequest(answer = -1)
                )

                Then("채점만 되고 정답률은 변경되지 않는다.") {
                    checkAnswerResponse.isAnswer shouldBe false
                    verify(exactly = 0) { userProducer.incorrectAnswer(any()) }
                }
            }
        }

        Given("유저가 퀴즈를 작성하는 중인 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
            }

            coEvery { userClient.findById(any()) } returns createFindUserByIdResponse()

            When("유저가 퀴즈를 제출하면") {
                val quizResponse = quizService.createQuiz(
                    userId = ID,
                    request = createCreateQuizRequest()
                )

                Then("퀴즈가 생성된다.") {
                    quizResponse shouldBeEqualToComparingFields QuizResponse(quiz)
                }
            }
        }
    }
}