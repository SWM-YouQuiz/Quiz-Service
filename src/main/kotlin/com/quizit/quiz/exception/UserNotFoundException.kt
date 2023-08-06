package com.quizit.quiz.exception

import com.quizit.quiz.global.exception.ServerException

class UserNotFoundException(
    override val message: String = "유저를 찾을 수 없습니다."
) : ServerException(code = 404, message)