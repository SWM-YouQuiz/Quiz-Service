package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.CreateChapterRequest
import com.youquiz.quiz.dto.UpdateChapterRequest
import com.youquiz.quiz.service.ChapterService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ChapterHandler(
    private val chapterService: ChapterService
) {
    suspend fun findAllByCourseId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(chapterService.findAllByCourseId(it))
        }

    suspend fun createChapter(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateChapterRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(chapterService.createChapter(it))
        }

    suspend fun updateChapter(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val updateChapterRequest = awaitBody<UpdateChapterRequest>()

            ServerResponse.ok().bodyValueAndAwait(chapterService.updateChapter(id, updateChapterRequest))
        }

    suspend fun deleteChapter(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            chapterService.deleteChapter(it)

            ServerResponse.ok().buildAndAwait()
        }
}