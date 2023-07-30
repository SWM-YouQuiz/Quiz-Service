package com.youquiz.quiz.event

data class CorrectAnswerEvent(
    val userId: String,
    val quizId: String
)