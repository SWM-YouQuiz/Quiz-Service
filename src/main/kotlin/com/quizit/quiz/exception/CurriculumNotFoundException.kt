package com.quizit.quiz.exception

import com.quizit.quiz.global.exception.ServerException

class CurriculumNotFoundException(
    override val message: String = "커리큘럼을 찾을 수 없습니다."
) : ServerException(code = 404, message)