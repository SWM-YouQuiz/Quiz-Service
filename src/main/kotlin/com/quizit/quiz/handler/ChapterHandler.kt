package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.global.annotation.Handler
import com.quizit.quiz.global.util.authentication
import com.quizit.quiz.service.ChapterService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Handler
class ChapterHandler(
    private val chapterService: ChapterService
) {
    fun getChapterById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(chapterService.getChapterById(request.pathVariable("id")))

    fun getChaptersByCourseId(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(chapterService.getChaptersByCourseId(request.pathVariable("id")))

    fun getProgressById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            authentication()
                .flatMap {
                    ServerResponse.ok()
                        .body(chapterService.getProgressById(pathVariable("id"), it.id))
                }
        }

    fun createChapter(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono<CreateChapterRequest>()
            .flatMap {
                ServerResponse.ok()
                    .body(chapterService.createChapter(it))
            }

    fun updateChapterById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<UpdateChapterByIdRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(chapterService.updateChapterById(pathVariable("id"), it))
                }
        }

    fun deleteChapterById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(chapterService.deleteChapterById(request.pathVariable("id")))
}