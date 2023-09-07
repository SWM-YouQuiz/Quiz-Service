package com.quizit.quiz.dto.request

data class CreateChapterRequest(
    val description: String,
    val document: String,
    val courseId: String
)