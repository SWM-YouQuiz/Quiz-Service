package com.youquiz.quiz.domain

import com.youquiz.quiz.fixture.createQuiz
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan

class QuizTest : BehaviorSpec() {
    init {
        Given("퀴즈가 존재하는 경우") {
            val quiz = createQuiz()

            When("유저가 해당 퀴즈의 정답을 맞췄다면") {
                val increasedQuiz = createQuiz().apply {
                    correctCount += 1
                    changeAnswerRate()
                }

                Then("해당 퀴즈의 정답률이 상승한다.") {
                    increasedQuiz.answerRate shouldBeGreaterThan quiz.answerRate
                }
            }
        }
    }
}