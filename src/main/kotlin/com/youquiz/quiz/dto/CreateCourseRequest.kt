package com.youquiz.quiz.dto

import com.youquiz.quiz.domain.Course

data class CreateCourseRequest(
    val title: String,
    val image: String
) {
    fun toEntity(): Course =
        Course(
            title = title,
            image = image
        )
}