package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.request.CreateCourseRequest
import com.youquiz.quiz.dto.request.UpdateCourseByIdRequest
import com.youquiz.quiz.service.CourseService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class CourseHandler(
    private val courseService: CourseService
) {
    suspend fun getCourses(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyAndAwait(courseService.getCourses())

    suspend fun createCourse(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateCourseRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(courseService.createCourse(it))
        }

    suspend fun updateCourseById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val updateCourseByIdRequest = awaitBody<UpdateCourseByIdRequest>()

            ServerResponse.ok().bodyValueAndAwait(courseService.updateCourseById(id, updateCourseByIdRequest))
        }

    suspend fun deleteCourseById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            courseService.deleteCourseById(it)

            ServerResponse.ok().buildAndAwait()
        }
}