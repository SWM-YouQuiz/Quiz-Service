package com.youquiz.quiz.domain

import com.youquiz.quiz.fixture.createQuiz
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan

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
        }
    }
}