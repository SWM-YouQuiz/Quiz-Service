package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse

const val DESCRIPTION = "test"

fun createCreateChapterRequest(
    description: String = DESCRIPTION,
    courseId: String = ID
): CreateChapterRequest =
    CreateChapterRequest(
        description = description,
        courseId = courseId
    )

fun createUpdateChapterByIdRequest(
    description: String = DESCRIPTION,
    courseId: String = ID
): UpdateChapterByIdRequest =
    UpdateChapterByIdRequest(
        description = description,
        courseId = courseId
    )

fun createChapterResponse(
    id: String = ID,
    description: String = DESCRIPTION,
    courseId: String = ID
): ChapterResponse =
    ChapterResponse(
        id = id,
        description = description,
        courseId = courseId
    )

fun createChapter(
    id: String = ID,
    description: String = DESCRIPTION,
    courseId: String = ID
): Chapter =
    Chapter(
        id = id,
        description = description,
        courseId = courseId
    )