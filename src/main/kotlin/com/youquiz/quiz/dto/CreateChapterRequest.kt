package com.youquiz.quiz.dto

data class CreateChapterRequest(
    val description: String,
    val courseId: String
)