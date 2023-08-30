package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Curriculum
import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.dto.response.CurriculumResponse

fun createCreateCurriculumRequest(
    title: String = TITLE,
    image: String = IMAGE
): CreateCurriculumRequest =
    CreateCurriculumRequest(
        title = title,
        image = image
    )

fun createUpdateCurriculumByIdRequest(
    title: String = TITLE,
    image: String = IMAGE
): UpdateCurriculumByIdRequest =
    UpdateCurriculumByIdRequest(
        title = title,
        image = image
    )

fun createCurriculumResponse(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE
): CurriculumResponse =
    CurriculumResponse(
        id = id,
        title = title,
        image = image
    )

fun createCurriculum(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE
): Curriculum =
    Curriculum(
        id = id,
        title = title,
        image = image
    )