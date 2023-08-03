package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.CheckAnswerRequest
import com.youquiz.quiz.dto.CreateQuizRequest
import com.youquiz.quiz.global.config.awaitAuthentication
import com.youquiz.quiz.service.QuizService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class QuizHandler(
    private val quizService: QuizService
) {
    suspend fun getQuizzesByChapterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByChapterId(it))
        }

    suspend fun getQuizzesByWriterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByWriterId(it))
        }

    suspend fun getQuizzesLikedQuiz(request: ServerRequest): ServerResponse =
        request.awaitAuthentication().run {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesLikedQuiz(id))
        }

    suspend fun createQuiz(request: ServerRequest): ServerResponse =
        with(request) {
            val userId = awaitAuthentication().id
            val createQuizRequest = awaitBody<CreateQuizRequest>()

            ServerResponse.ok().bodyValueAndAwait(quizService.createQuiz(userId, createQuizRequest))
        }

    suspend fun checkAnswer(request: ServerRequest): ServerResponse =
        with(request) {
            val userId = awaitAuthentication().id
            val checkAnswerRequest = awaitBody<CheckAnswerRequest>()

            ServerResponse.ok().bodyValueAndAwait(quizService.checkAnswer(userId, checkAnswerRequest))
        }
}