package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CreateCourseRequest
import com.quizit.quiz.dto.request.UpdateCourseByIdRequest
import com.quizit.quiz.global.annotation.Handler
import com.quizit.quiz.global.util.authentication
import com.quizit.quiz.service.CourseService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class CourseHandler(
    private val courseService: CourseService
) {
    fun getCourseById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(courseService.getCourseById(request.pathVariable("id")))

    fun getCoursesByCurriculumId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(courseService.getCoursesByCurriculumId(request.pathVariable("id")))

    fun getProgressById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            authentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(courseService.getProgressById(pathVariable("id"), it.id))
                }
        }

    fun createCourse(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<CreateCourseRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(courseService.createCourse(it))
            }

    fun updateCourseById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<UpdateCourseByIdRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(courseService.updateCourseById(pathVariable("id"), it))
                }
        }

    fun deleteCourseById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(courseService.deleteCourseById(request.pathVariable("id")))
}