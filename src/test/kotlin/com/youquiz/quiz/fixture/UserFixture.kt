package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.User

const val NICKNAME = "earlgrey02"

fun createUser(
    id: Long = ID,
    nickname: String = NICKNAME
): User = User(
    id = id,
    nickname = nickname
)