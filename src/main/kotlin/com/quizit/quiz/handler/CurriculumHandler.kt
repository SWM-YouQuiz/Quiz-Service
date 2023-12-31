package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.global.annotation.Handler
import com.quizit.quiz.global.util.authentication
import com.quizit.quiz.service.CurriculumService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class CurriculumHandler(
    private val curriculumService: CurriculumService
) {
    fun getCurriculumById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(curriculumService.getCurriculumById(request.pathVariable("id")))

    fun getCurriculums(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(curriculumService.getCurriculums())

    fun getProgressById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            authentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(curriculumService.getProgressById(pathVariable("id"), it.id))
                }
        }

    fun createCurriculum(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<CreateCurriculumRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(curriculumService.createCurriculum(it))
            }

    fun updateCurriculumById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<UpdateCurriculumByIdRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(curriculumService.updateCurriculumById(pathVariable("id"), it))
                }
        }

    fun deleteCurriculumById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(curriculumService.deleteCurriculumById(request.pathVariable("id")))
}