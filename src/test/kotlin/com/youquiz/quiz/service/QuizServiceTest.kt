package com.youquiz.quiz.service

import com.youquiz.quiz.dto.FindAllMarkedQuizRequest
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.fixture.CHAPTER_ID
import com.youquiz.quiz.fixture.ID
import com.youquiz.quiz.fixture.createQuiz
import com.youquiz.quiz.repository.QuizRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class QuizServiceTest : BehaviorSpec() {
    private val quizRepository = mockk<QuizRepository>()

    private val quizService = QuizService(quizRepository)

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
    }
}