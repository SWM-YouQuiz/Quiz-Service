package com.youquiz.quiz.dto.event

data class CheckAnswerEvent(
    val userId: String,
    val quizId: String,
    val isAnswer: Boolean
)