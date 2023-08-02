package com.youquiz.quiz.exception

import com.youquiz.quiz.global.exception.ServerException

class CourseNotFoundException(
    override val message: String = "코스를 찾을 수 없습니다."
) : ServerException(code = 404, message)