package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.enum.Provider
import com.quizit.quiz.domain.enum.Role
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val email: String,
    val username: String,
    val image: String?,
    val level: Int,
    val role: Role,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val answerRate: Double,
    val provider: Provider,
    val createdDate: LocalDateTime,
    val correctQuizIds: HashSet<String>,
    val incorrectQuizIds: HashSet<String>,
    val markedQuizIds: HashSet<String>
)