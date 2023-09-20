package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.service.CurriculumService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class CurriculumHandler(
    private val curriculumService: CurriculumService
) {
    suspend fun getCurriculumById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyValueAndAwait(curriculumService.getCurriculumById(it))
        }

    suspend fun getCurriculums(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyAndAwait(curriculumService.getCurriculums())

    suspend fun createCurriculum(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateCurriculumRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(curriculumService.createCurriculum(it))
        }

    suspend fun updateCurriculumById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val updateCurriculumByIdRequest = awaitBody<UpdateCurriculumByIdRequest>()

            ServerResponse.ok()
                .bodyValueAndAwait(curriculumService.updateCurriculumById(id, updateCurriculumByIdRequest))
        }

    suspend fun deleteCurriculumById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            curriculumService.deleteCurriculumById(it)

            ServerResponse.ok().buildAndAwait()
        }
}