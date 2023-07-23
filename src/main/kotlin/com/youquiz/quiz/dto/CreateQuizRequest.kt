package com.youquiz.quiz.dto

data class CreateQuizRequest(
    val question: String,
    val answer: Int,
    val solution: String,
    val writerId: Long,
    val chapterId: String,
) {
}