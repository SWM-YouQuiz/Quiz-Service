package com.youquiz.quiz.dto.response

import com.youquiz.quiz.domain.Chapter

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