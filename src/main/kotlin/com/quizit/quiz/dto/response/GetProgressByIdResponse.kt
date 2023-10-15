package com.quizit.quiz.dto.response

data class GetProgressByIdResponse(
    val total: Long,
    val solved: Long
)