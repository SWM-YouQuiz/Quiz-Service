package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.adapter.producer.QuizProducer
import com.quizit.quiz.dto.response.QuizResponse
import com.quizit.quiz.fixture.*
import com.quizit.quiz.repository.QuizCacheRepository
import com.quizit.quiz.repository.QuizRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.mockk.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class QuizServiceTest : BehaviorSpec() {
    private val quizRepository = mockk<QuizRepository>()

    private val quizCacheRepository = mockk<QuizCacheRepository>().apply {
        coEvery { save(any()) } returns true
    }

    private val userClient = mockk<UserClient>()

    private val quizProducer = mockk<QuizProducer>().apply {
        coEvery { checkAnswer(any()) } just runs
        coEvery { markQuiz(any()) } just runs
    }

    private val quizService = QuizService(
        quizRepository = quizRepository,
        quizCacheRepository = quizCacheRepository,
        userClient = userClient,
        quizProducer = quizProducer
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("챕터와 각각의 챕터에 속하는 퀴즈들이 존재하는 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.deleteById(any()) } just runs
                coEvery { quizCacheRepository.findById(any()) } returns it
                coEvery { quizCacheRepository.deleteById(any()) } returns true
            }
            val quizzes = listOf(quiz).apply {
                asFlow().let {
                    coEvery {
                        quizRepository.findAllByChapterIdAndAnswerRateBetween(any(), any(), any(), any())
                    } returns it
                    coEvery { quizRepository.findAllByIdIn(any()) } returns it
                    coEvery { quizRepository.findAllByQuestionContains(any()) } returns it
                }
            }
            val updateQuizByIdRequest = createUpdateQuizByIdRequest(question = "update").apply {
                createQuiz(question = question).let {
                    coEvery { quizRepository.save(any()) } returns it
                }
            }

            When("유저가 특정 퀴즈를 조회하면") {
                val quizResponse = quizService.getQuizById(ID)

                Then("해당 퀴즈가 조회된다.") {
                    quizResponse shouldBeEqualToComparingFields QuizResponse(quiz)
                }
            }

            When("유저가 특정 퀴즈를 문제 지문을 통해 검색하면") {
                val quizResponses = quizService.getQuizzesByQuestionContains(QUESTION).toList()

                Then("해당 키워드가 들어간 문제 지문을 가진 퀴즈가 조회된다.") {
                    quizResponses shouldContainExactly quizzes.map { QuizResponse(it) }
                }
            }

            When("유저가 챕터를 들어가면") {
                val quizResponses =
                    quizService.getQuizzesByChapterIdAndAnswerRateRange(ID, setOf(0.0, 100.0), PAGEABLE).toList()

                Then("해당 챕터에 속하는 퀴즈들이 주어진다.") {
                    quizzes.map { QuizResponse(it) }.let {
                        quizResponses shouldContainExactly it
                    }
                }
            }

            When("유저가 특정 퀴즈를 수정하면") {
                val quizResponse = quizService.updateQuizById(ID, createJwtAuthentication(), updateQuizByIdRequest)

                Then("해당 퀴즈가 수정된다.") {
                    quizResponse.question shouldNotBeEqual quiz.question
                }
            }

            When("유저가 특정 퀴즈를 삭제하면") {
                quizService.deleteQuizById(ID, createJwtAuthentication())

                Then("해당 퀴즈가 삭제된다.") {
                    coVerify { quizRepository.deleteById(any()) }
                }
            }
        }

        Given("유저가 저장한 퀴즈가 존재하는 경우") {
            val quizzes = listOf(createQuiz()).apply {
                asFlow().let {
                    coEvery { quizRepository.findAllByIdIn(any()) } returns it
                }
            }

            coEvery { userClient.getUserById(any()) } returns createUserResponse()

            When("유저가 본인이 저장한 퀴즈 보관함에 들어가면") {
                val quizResponses = quizService.getMarkedQuizzes(ID).toList()

                Then("유저가 저장한 퀴즈들이 주어진다.") {
                    quizResponses shouldContainExactly quizzes.map { QuizResponse(it) }
                }
            }
        }

        Given("유저가 작성한 퀴즈가 존재하는 경우") {
            val quizzes = listOf(createQuiz()).apply {
                asFlow().let {
                    coEvery { quizRepository.findAllByWriterId(any()) } returns it
                }
            }

            When("유저가 본인이 작성한 퀴즈 보관함에 들어가면") {
                val quizResponses = quizService.getQuizzesByWriterId(ID).toList()

                Then("본인이 작성한 퀴즈들이 주어진다.") {
                    quizResponses shouldContainExactly quizzes.map { QuizResponse(it) }
                }
            }
        }

        Given("유저가 퀴즈를 푼 경우") {
            createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { quizCacheRepository.findById(any()) } returns it
            }

            coEvery { userClient.getUserById(any()) } returns createUserResponse()

            When("옳은 답을 제출하면") {
                quizService.checkAnswer(ID, ID, createCheckAnswerRequest())

                Then("정답으로 처리되어 정답률이 변경된다.") {
                    verify { quizProducer.checkAnswer(any()) }
                }
            }

            When("틀린 답을 제출하면") {
                quizService.checkAnswer(ID, ID, createCheckAnswerRequest(answer = -1))

                Then("오답으로 처리되어 정답률이 변경된다.") {
                    verify { quizProducer.checkAnswer(any()) }
                }
            }
        }

        Given("유저가 이미 푼 퀴즈가 존재하는 경우") {
            val quiz = createQuiz(id = "quiz").also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { quizCacheRepository.findById(any()) } returns it
            }

            coEvery { userClient.getUserById(any()) } returns createUserResponse()

            When("해당 퀴즈를 풀고 정답을 제출하면") {
                quizService.checkAnswer(quiz.id!!, ID, createCheckAnswerRequest())

                Then("채점만 되고 정답률은 변경되지 않는다.") {
                    verify(exactly = 0) { quizProducer.checkAnswer(any()) }
                }
            }

            When("해당 퀴즈를 풀고 오답을 제출하면") {
                quizService.checkAnswer(quiz.id!!, ID, createCheckAnswerRequest(answer = -1))

                Then("채점만 되고 정답률은 변경되지 않는다.") {
                    verify(exactly = 0) { quizProducer.checkAnswer(any()) }
                }
            }
        }

        Given("유저가 퀴즈를 작성하는 중인 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
            }

            When("유저가 퀴즈를 제출하면") {
                val quizResponse = quizService.createQuiz(ID, createCreateQuizRequest())

                Then("퀴즈가 생성된다.") {
                    quizResponse shouldBeEqualToComparingFields QuizResponse(quiz)
                }
            }
        }

        Given("유저가 퀴즈를 저장하는 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { quizCacheRepository.findById(any()) } returns it
            }

            When("유저가 해당 퀴즈를 처음 저장한다면") {
                quizService.markQuiz(ID, ID)

                Then("퀴즈가 저장된다.") {
                    quiz.markedUserIds.size shouldBeGreaterThan createQuiz().markedUserIds.size
                }
            }

            When("이미 유저가 해당 퀴즈를 저장한 상태라면") {
                quizService.markQuiz(ID, quiz.markedUserIds.random())

                Then("퀴즈가 저장 취소된다.") {
                    quiz.markedUserIds.size shouldBeLessThan createQuiz().markedUserIds.size
                }
            }
        }

        Given("유저가 퀴즈를 좋아요로 평가하는 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { quizCacheRepository.findById(any()) } returns it
            }

            When("유저가 해당 퀴즈를 처음 좋아요로 평가한다면") {
                quizService.evaluateQuiz(ID, ID, true)

                Then("퀴즈가 평가된다.") {
                    quiz.likedUserIds.size shouldBeGreaterThan createQuiz().likedUserIds.size
                }
            }

            When("유저가 이미 해당 퀴즈를 좋아요로 평가한 상태라면") {
                quizService.evaluateQuiz(ID, quiz.likedUserIds.random(), true)

                Then("퀴즈 평가가 취소된다.") {
                    quiz.likedUserIds.size shouldBeLessThan createQuiz().likedUserIds.size
                }
            }
        }

        Given("유저가 퀴즈를 싫어요로 평가하는 경우") {
            val quiz = createQuiz().also {
                coEvery { quizRepository.save(any()) } returns it
                coEvery { quizCacheRepository.findById(any()) } returns it
            }

            When("유저가 해당 퀴즈를 처음 싫어요로 평가한다면") {
                quizService.evaluateQuiz(ID, ID, false)

                Then("퀴즈가 평가된다.") {
                    quiz.unlikedUserIds.size shouldBeGreaterThan createQuiz().unlikedUserIds.size
                }
            }

            When("유저가 이미 해당 퀴즈를 좋아요로 평가한 상태라면") {
                quizService.evaluateQuiz(ID, quiz.unlikedUserIds.random(), false)

                Then("퀴즈 평가가 취소된다.") {
                    quiz.unlikedUserIds.size shouldBeLessThan createQuiz().unlikedUserIds.size
                }
            }
        }
    }
}