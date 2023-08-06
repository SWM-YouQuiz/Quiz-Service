package com.quizit.quiz.dto.request

data class UpdateQuizByIdRequest(
    val question: String,
    val answer: Int,
    val solution: String,
    val chapterId: String,
    val options: List<String>,
)