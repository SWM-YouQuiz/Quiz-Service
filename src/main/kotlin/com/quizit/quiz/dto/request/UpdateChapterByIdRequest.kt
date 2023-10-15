package com.quizit.quiz.dto.request

data class UpdateChapterByIdRequest(
    val description: String,
    val document: String,
    val courseId: String,
    val image: String,
    val index: Int
)