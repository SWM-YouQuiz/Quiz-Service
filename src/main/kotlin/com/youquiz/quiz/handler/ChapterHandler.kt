package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.request.CreateChapterRequest
import com.youquiz.quiz.dto.request.UpdateChapterByIdRequest
import com.youquiz.quiz.service.ChapterService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ChapterHandler(
    private val chapterService: ChapterService
) {
    suspend fun getChaptersByCourseId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(chapterService.getChaptersByCourseId(it))
        }

    suspend fun createChapter(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateChapterRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(chapterService.createChapter(it))
        }

    suspend fun updateChapterById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val updateChapterByIdRequest = awaitBody<UpdateChapterByIdRequest>()

            ServerResponse.ok().bodyValueAndAwait(chapterService.updateChapterById(id, updateChapterByIdRequest))
        }

    suspend fun deleteChapterById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            chapterService.deleteChapterById(it)

            ServerResponse.ok().buildAndAwait()
        }
}