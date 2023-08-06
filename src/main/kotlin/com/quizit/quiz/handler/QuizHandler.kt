package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CheckAnswerRequest
import com.quizit.quiz.dto.request.CreateQuizRequest
import com.quizit.quiz.dto.request.UpdateQuizByIdRequest
import com.quizit.quiz.global.config.awaitAuthentication
import com.quizit.quiz.service.QuizService
import org.springframework.data.domain.PageRequest
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
        with(request) {
            val chapterId = pathVariable("id")
            val page = queryParamOrNull("page")?.toInt()
            val size = queryParamOrNull("size")?.toInt()

            ServerResponse.ok().bodyAndAwait(
                if ((page != null) && (size != null)) {
                    quizService.getQuizzesByChapterId(chapterId, PageRequest.of(page, size))
                } else {
                    quizService.getQuizzesByChapterId(chapterId)
                }
            )

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