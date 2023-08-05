package com.youquiz.quiz.handler

import com.youquiz.quiz.dto.request.CheckAnswerRequest
import com.youquiz.quiz.dto.request.CreateQuizRequest
import com.youquiz.quiz.dto.request.UpdateQuizByIdRequest
import com.youquiz.quiz.global.config.awaitAuthentication
import com.youquiz.quiz.service.QuizService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class QuizHandler(
    private val quizService: QuizService
) {
    suspend fun getQuizById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyValueAndAwait(quizService.getQuizById(it))
        }

    suspend fun getQuizzesByChapterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByChapterId(it))
        }

    suspend fun getQuizzesByWriterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByWriterId(it))
        }

    suspend fun getQuizzesLikedQuiz(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesLikedQuiz(it))
        }

    suspend fun createQuiz(request: ServerRequest): ServerResponse =
        with(request) {
            val userId = awaitAuthentication().id
            val createQuizRequest = awaitBody<CreateQuizRequest>()

            ServerResponse.ok().bodyValueAndAwait(quizService.createQuiz(userId, createQuizRequest))
        }

    suspend fun updateQuizById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val authentication = awaitAuthentication()
            val updateQuizByIdRequest = awaitBody<UpdateQuizByIdRequest>()

            ServerResponse.ok().bodyValueAndAwait(quizService.updateQuizById(id, authentication, updateQuizByIdRequest))
        }

    suspend fun deleteQuizById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val authentication = awaitAuthentication()

            quizService.deleteQuizById(id, authentication)

            ServerResponse.ok().buildAndAwait()
        }

    suspend fun checkAnswer(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val userId = awaitAuthentication().id
            val checkAnswerRequest = awaitBody<CheckAnswerRequest>()

            ServerResponse.ok().bodyValueAndAwait(quizService.checkAnswer(id, userId, checkAnswerRequest))
        }

    suspend fun likeQuiz(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val userId = awaitAuthentication().id

            quizService.likeQuiz(id, userId)

            ServerResponse.ok().buildAndAwait()
        }
}