package com.youquiz.quiz.event

data class IncorrectAnswerEvent(
    val userId: String,
    val quizId: String
)