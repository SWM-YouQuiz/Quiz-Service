package com.quizit.quiz.dto.response

import java.time.LocalDateTime

data class GetUserByIdResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val role: String,
    val allowPush: Boolean,
    val createdDate: LocalDateTime,
    val correctQuizIds: Set<String>,
    val incorrectQuizIds: Set<String>,
    val markedQuidIds: Set<String>
)