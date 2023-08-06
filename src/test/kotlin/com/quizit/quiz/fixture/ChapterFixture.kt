package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse

const val DESCRIPTION = "test"
const val COURSE_ID = "test"

fun createCreateChapterRequest(
    description: String = DESCRIPTION,
    courseId: String = COURSE_ID
): CreateChapterRequest =
    CreateChapterRequest(
        description = description,
        courseId = courseId
    )

fun createUpdateChapterByIdRequest(
    description: String = DESCRIPTION,
): UpdateChapterByIdRequest =
    UpdateChapterByIdRequest(
        description = description,
    )

fun createChapterResponse(
    id: String = ID,
    description: String = DESCRIPTION,
    courseId: String = COURSE_ID
): ChapterResponse =
    ChapterResponse(
        id = id,
        description = description,
        courseId = courseId
    )

fun createChapter(
    id: String = ID,
    description: String = DESCRIPTION,
    courseId: String = COURSE_ID
): Chapter =
    Chapter(
        id = id,
        description = description,
        courseId = courseId
    )