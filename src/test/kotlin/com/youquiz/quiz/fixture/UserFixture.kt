package com.youquiz.quiz.fixture

import com.youquiz.quiz.dto.GetUserByIdResponse
import java.time.LocalDateTime

const val USERNAME = "test"
const val NICKNAME = "test"
const val PASSWORD = "root"
const val ROLE = "USER"
const val ALLOW_PUSH = true
val CORRECT_QUIZ_IDS = setOf("quiz")
val INCORRECT_QUIZ_IDS = setOf("quiz")
val LIKED_QUIZ_IDS = setOf("quiz")

fun createGetUserByIdResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    role: String = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    createdDate: LocalDateTime = LocalDateTime.now(),
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    likedQuizIds: Set<String> = LIKED_QUIZ_IDS
): GetUserByIdResponse =
    GetUserByIdResponse(
        id = id,
        username = username,
        nickname = nickname,
        role = role,
        allowPush = allowPush,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        likedQuizIds = likedQuizIds
    )