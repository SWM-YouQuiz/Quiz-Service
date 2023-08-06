package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Chapter

data class ChapterResponse(
    val id: String,
    val description: String,
    val courseId: String
) {
    companion object {
        operator fun invoke(chapter: Chapter): ChapterResponse =
            with(chapter) {
                ChapterResponse(
                    id = id!!,
                    description = description,
                    courseId = courseId
                )
            }
    }
}