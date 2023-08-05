package com.youquiz.quiz.event

data class LikeEvent(
    val userId: String,
    val quizId: String,
    val isLike: Boolean
)