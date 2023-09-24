package com.quizit.quiz.domain

import com.quizit.quiz.fixture.DESCRIPTION
import com.quizit.quiz.fixture.DOCUMENT
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createChapter
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields

class ChapterTest : BehaviorSpec() {
    init {
        Given("챕터가 존재하는 경우") {
            val chapter = createChapter()
                .apply {
                    description = DESCRIPTION
                    document = DOCUMENT
                    courseId = ID
                } // Code Coverage

            When("어드민이 챕터를 수정하면") {
                val updatedChapter = createChapter(description = "updated_description")
                    .apply { update(description, document, courseId) }

                Then("챕터가 수정된다.") {
                    updatedChapter shouldNotBeEqualToComparingFields chapter
                }
            }
        }
    }
}