package com.quizit.quiz.exception

import com.quizit.quiz.global.exception.ServerException

data class ChapterNotFoundException(
    override val message: String = "챕터를 찾을 수 없습니다."
) : ServerException(code = 404, message)