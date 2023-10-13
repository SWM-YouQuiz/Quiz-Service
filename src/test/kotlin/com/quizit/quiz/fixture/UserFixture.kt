package com.quizit.quiz.fixture

import com.quizit.quiz.domain.enum.Provider
import com.quizit.quiz.domain.enum.Role
import com.quizit.quiz.dto.response.UserResponse
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val IMAGE = "http://localhost:8080/image.jpg"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
val PROVIDER = Provider.KAKAO
val CORRECT_QUIZ_IDS = hashSetOf("1")
val INCORRECT_QUIZ_IDS = hashSetOf("1")
val MARKED_QUIZ_IDS = hashSetOf("1")

fun createUserResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    image: String? = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider? = PROVIDER,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS,
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
        provider = provider,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        markedQuizIds = markedQuizIds
    )