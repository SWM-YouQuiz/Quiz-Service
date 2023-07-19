package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.FindAllMarkedQuizRequest
import com.youquiz.quiz.service.QuizService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait

@Component
class QuizHandler(
    private val quizService: QuizService
) {
    suspend fun findAllByChapterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.findAllByChapterId(it))
        }

    suspend fun findAllByWriterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.findAllByWriterId(it.toLong()))
        }

    suspend fun findAllMarkedQuiz(request: ServerRequest): ServerResponse =
        request.awaitBody<FindAllMarkedQuizRequest>().let {
            ServerResponse.ok().bodyAndAwait(quizService.findAllMarkedQuiz(it))
        }
}