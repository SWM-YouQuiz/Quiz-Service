package com.quizit.quiz.exception

import com.quizit.quiz.global.exception.ServerException

data class QuizNotFoundException(
    override val message: String = "퀴즈를 찾을 수 없습니다."
) : ServerException(code = 404, message)