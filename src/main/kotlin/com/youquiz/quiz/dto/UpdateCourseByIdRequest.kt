package com.youquiz.quiz.dto

import com.youquiz.quiz.domain.Course

data class UpdateCourseByIdRequest(
    val title: String,
    val image: String
) {
    fun toEntity(id: String): Course =
        Course(
            id = id,
            title = title,
            image = image
        )
}