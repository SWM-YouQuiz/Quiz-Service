package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.Chapter
import com.youquiz.quiz.dto.ChapterResponse
import com.youquiz.quiz.dto.CreateChapterRequest
import com.youquiz.quiz.dto.UpdateChapterRequest

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

fun createUpdateChapterRequest(
    description: String = DESCRIPTION,
): UpdateChapterRequest =
    UpdateChapterRequest(
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