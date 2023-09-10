package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse

const val DESCRIPTION = "test"
const val DOCUMENT = "test"

fun createCreateChapterRequest(
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID
): CreateChapterRequest =
    CreateChapterRequest(
        description = description,
        document = document,
        courseId = courseId
    )

fun createUpdateChapterByIdRequest(
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID
): UpdateChapterByIdRequest =
    UpdateChapterByIdRequest(
        description = description,
        document = document,
        courseId = courseId
    )

fun createChapterResponse(
    id: String = ID,
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID
): ChapterResponse =
    ChapterResponse(
        id = id,
        description = description,
        document = document,
        courseId = courseId
    )

fun createChapter(
    id: String = ID,
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID
): Chapter =
    Chapter(
        id = id,
        description = description,
        document = document,
        courseId = courseId
    )