package com.quizit.quiz.dto.response

data class GetProgressByIdResponse(
    val total: Int,
    val solved: Int
)