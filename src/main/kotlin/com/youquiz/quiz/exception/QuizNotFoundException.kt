package com.youquiz.quiz.exception

import com.youquiz.quiz.global.exception.ServerException

class QuizNotFoundException(
    override val message: String = "퀴즈를 찾을 수 없습니다."
) : ServerException(code = 404, message)