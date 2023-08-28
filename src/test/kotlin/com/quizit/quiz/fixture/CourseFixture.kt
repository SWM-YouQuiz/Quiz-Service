package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Course
import com.quizit.quiz.dto.request.CreateCourseRequest
import com.quizit.quiz.dto.request.UpdateCourseByIdRequest
import com.quizit.quiz.dto.response.CourseResponse

const val TITLE = "test"
const val IMAGE = "test"
const val CURRICULUM_ID = "test"

fun createCreateCourseRequest(
    title: String = TITLE,
    image: String = IMAGE,
    curriculumId: String = CURRICULUM_ID
): CreateCourseRequest =
    CreateCourseRequest(
        title = title,
        image = image,
        curriculumId = curriculumId
    )

fun createUpdateCourseByIdRequest(
    title: String = TITLE,
    image: String = IMAGE,
    curriculumId: String = CURRICULUM_ID
): UpdateCourseByIdRequest =
    UpdateCourseByIdRequest(
        title = title,
        image = image,
        curriculumId = curriculumId
    )

fun createCourseResponse(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE,
    curriculumId: String = CURRICULUM_ID
): CourseResponse =
    CourseResponse(
        id = id,
        title = title,
        image = image,
        curriculumId = curriculumId
    )

fun createCourse(
    id: String = ID,
    title: String = TITLE,
    image: String = IMAGE,
    curriculumId: String = CURRICULUM_ID
): Course =
    Course(
        id = id,
        title = title,
        image = image,
        curriculumId = curriculumId
    )