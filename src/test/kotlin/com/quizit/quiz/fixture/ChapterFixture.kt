package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse

const val DESCRIPTION = "description"
const val DOCUMENT = "document"
const val INDEX = 1

fun createCreateChapterRequest(
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID,
    image: String = IMAGE,
    index: Int = INDEX
): CreateChapterRequest =
    CreateChapterRequest(
        description = description,
        document = document,
        courseId = courseId,
        image = image,
        index = index
    )

fun createUpdateChapterByIdRequest(
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID,
    image: String = IMAGE,
    index: Int = INDEX
): UpdateChapterByIdRequest =
    UpdateChapterByIdRequest(
        description = description,
        document = document,
        courseId = courseId,
        image = image,
        index = index
    )

fun createChapterResponse(
    id: String = ID,
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID,
    image: String = IMAGE,
    index: Int = INDEX
): ChapterResponse =
    ChapterResponse(
        id = id,
        description = description,
        document = document,
        courseId = courseId,
        image = image,
        index = index
    )

fun createChapter(
    id: String = ID,
    description: String = DESCRIPTION,
    document: String = DOCUMENT,
    courseId: String = ID,
    image: String = IMAGE,
    index: Int = INDEX
): Chapter =
    Chapter(
        id = id,
        description = description,
        document = document,
        courseId = courseId,
        image = image,
        index = index
    )