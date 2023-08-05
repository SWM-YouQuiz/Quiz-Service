package com.youquiz.quiz.dto.event

data class LikeQuizEvent(
    val userId: String,
    val quizId: String,
    val isLike: Boolean
)