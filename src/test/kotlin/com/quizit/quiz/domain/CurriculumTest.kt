package com.quizit.quiz.domain

import com.quizit.quiz.fixture.IMAGE
import com.quizit.quiz.fixture.TITLE
import com.quizit.quiz.fixture.createCurriculum
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields

class CurriculumTest : BehaviorSpec() {
    init {
        Given("커리큘럼이 존재하는 경우") {
            val curriculum = createCurriculum()
                .apply {
                    title = TITLE
                    image = IMAGE
                } // Code Coverage

            When("어드민이 커리큘럼을 수정하면") {
                val updatedCurriculum = createCurriculum(title = "updated_title")
                    .apply { update(title, image) }

                Then("커리큘럼이 수정된다.") {
                    updatedCurriculum shouldNotBeEqualToComparingFields curriculum
                }
            }
        }
    }
}