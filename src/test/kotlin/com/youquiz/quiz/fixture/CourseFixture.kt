package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.Course
import com.youquiz.quiz.dto.CourseResponse
import com.youquiz.quiz.dto.CreateCourseRequest
import com.youquiz.quiz.dto.UpdateCourseByIdRequest

const val TITLE = "test"
const val IMAGE = "test"

fun createCreateCourseRequest(
    title: String = TITLE,
    image: String = IMAGE
): CreateCourseRequest =
    CreateCourseRequest(
        title = title,
        image = image
    )

fun createUpdateCourseByIdRequest(
    title: String = TITLE,
    image: String = IMAGE
): UpdateCourseByIdRequest =
    UpdateCourseByIdRequest(
        title = title,
        image = image
    )

fun createCourseResponse(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE
): CourseResponse =
    CourseResponse(
        id = id,
        title = title,
        image = image
    )

fun createCourse(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE
): Course =
    Course(
        id = id,
        title = title,
        image = image
    )