package com.youquiz.quiz.dto

import com.youquiz.quiz.domain.Course

data class CourseResponse(
    val id: String,
    val title: String,
    val image: String
) {
    companion object {
        operator fun invoke(course: Course): CourseResponse =
            with(course) {
                CourseResponse(
                    id = id!!,
                    title = title,
                    image = image
                )
            }
    }
}