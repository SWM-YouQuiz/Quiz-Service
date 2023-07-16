package com.youquiz.quiz.global.exception

abstract class ServerException(
    val code: Int,
    override val message: String
) : RuntimeException(message)