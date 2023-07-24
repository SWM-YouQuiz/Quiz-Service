package com.youquiz.quiz.dto

data class CheckAnswerRequest(
    val quizId: String,
    val answer: Int
)