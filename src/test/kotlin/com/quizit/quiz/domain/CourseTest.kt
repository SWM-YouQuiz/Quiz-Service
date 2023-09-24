package com.quizit.quiz.domain

import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.IMAGE
import com.quizit.quiz.fixture.TITLE
import com.quizit.quiz.fixture.createCourse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields

class CourseTest : BehaviorSpec() {
    init {
        Given("코스가 존재하는 경우") {
            val course = createCourse()
                .apply {
                    title = TITLE
                    image = IMAGE
                    curriculumId = ID
                } // Code Coverage

            When("어드민이 코스를 수정하면") {
                val updatedCourse = createCourse(title = "updated_title")
                    .apply { update(title, image, curriculumId) }

                Then("코스가 수정된다.") {
                    updatedCourse shouldNotBeEqualToComparingFields course
                }
            }
        }
    }
}