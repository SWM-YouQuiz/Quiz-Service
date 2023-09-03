package com.quizit.quiz.domain

import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createQuiz
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan

class QuizTest : BehaviorSpec() {
    init {
        Given("퀴즈가 존재하는 경우") {
            val quiz = createQuiz().apply {
                incorrectCount = 0
                correctCount = 0
            } // Code Coverage

            When("유저가 해당 퀴즈의 정답을 맞췄다면") {
                val increasedQuiz = createQuiz().apply { correctAnswer() }

                Then("해당 퀴즈의 정답률이 상승한다.") {
                    increasedQuiz.answerRate shouldBeGreaterThan quiz.answerRate
                }
            }

            When("유저가 해당 퀴즈의 정답을 틀렸다면") {
                val decreasedQuiz = createQuiz().apply { incorrectAnswer() }

                Then("해당 퀴즈의 정답률이 감소한다.") {
                    decreasedQuiz.answerRate shouldBeLessThan quiz.answerRate
                }
            }

            When("유저가 해당 퀴즈를 저장했다면") {
                val markedQuiz = createQuiz().apply { mark(ID) }

                Then("해당 퀴즈에 저장한 유저가 추가된다.") {
                    markedQuiz.markedUserIds.size shouldBeGreaterThan quiz.markedUserIds.size
                }
            }

            When("유저가 해당 퀴즈를 저장 취소를 했다면") {
                val unmarkedQuiz = createQuiz().apply { unmark(markedUserIds.random()) }

                Then("해당 퀴즈에 저장한 유저가 삭제된다.") {
                    unmarkedQuiz.markedUserIds.size shouldBeLessThan quiz.markedUserIds.size
                }
            }
        }
    }
}