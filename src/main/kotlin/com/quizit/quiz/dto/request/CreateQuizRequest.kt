package com.quizit.quiz.dto.request

data class CreateQuizRequest(
    val question: String,
    val answer: Int,
    val solution: String,
    val chapterId: String,
    val options: List<String>
)