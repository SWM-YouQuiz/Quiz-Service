package com.quizit.quiz.fixture

import com.quizit.quiz.dto.response.UserResponse
import java.time.LocalDateTime

const val USERNAME = "test"
const val NICKNAME = "test"
const val LEVEL = 2
const val ROLE = "USER"
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
val CORRECT_QUIZ_IDS = setOf("quiz")
val INCORRECT_QUIZ_IDS = setOf("quiz")
val MARKED_QUIZ_IDS = setOf("quiz")

fun createUserResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    image: String = IMAGE,
    level: Int = LEVEL,
    role: String = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: Set<String> = MARKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        username = username,
        nickname = nickname,
        image = image,
        level = level,
        role = role,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        answerRate = answerRate,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        markedQuizIds = markedQuizIds
    )