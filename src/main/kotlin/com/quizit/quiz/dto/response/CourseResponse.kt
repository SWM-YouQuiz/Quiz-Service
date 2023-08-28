package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Course

data class CourseResponse(
    val id: String,
    val title: String,
    val image: String,
    val curriculumId: String
) {
    companion object {
        operator fun invoke(course: Course): CourseResponse =
            with(course) {
                CourseResponse(
                    id = id!!,
                    title = title,
                    image = image,
                    curriculumId = curriculumId
                )
            }
    }
}