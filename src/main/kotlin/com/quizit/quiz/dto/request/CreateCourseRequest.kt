package com.quizit.quiz.dto.request

data class CreateCourseRequest(
    val title: String,
    val image: String,
    val curriculumId: String
)