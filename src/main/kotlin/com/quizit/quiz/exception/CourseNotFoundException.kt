package com.quizit.quiz.exception

import com.quizit.quiz.global.exception.ServerException

data class CourseNotFoundException(
    override val message: String = "코스를 찾을 수 없습니다."
) : ServerException(code = 404, message)