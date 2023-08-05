package com.youquiz.quiz.dto.request

data class UpdateQuizByIdRequest(
    val question: String,
    val answer: Int,
    val solution: String,
    val chapterId: String,
    val options: List<String>,
)