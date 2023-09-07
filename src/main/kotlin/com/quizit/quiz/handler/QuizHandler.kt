package com.quizit.quiz.handler

import com.quizit.quiz.dto.request.CheckAnswerRequest
import com.quizit.quiz.dto.request.CreateQuizRequest
import com.quizit.quiz.dto.request.UpdateQuizByIdRequest
import com.quizit.quiz.global.config.awaitAuthentication
import com.quizit.quiz.global.util.queryParamNotNull
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

    suspend fun getQuizzesByChapterIdAndAnswerRateRange(request: ServerRequest): ServerResponse =
        with(request) {
            val chapterId = pathVariable("id")
            val page = queryParamNotNull<Int>("page")
            val size = queryParamNotNull<Int>("size")
            val answerRateRange = queryParamNotNull<String>("range").split(",").map { it.toDouble() }.toSet()

            ServerResponse.ok()
                .bodyAndAwait(
                    quizService.getQuizzesByChapterIdAndAnswerRateRange(
                        chapterId, answerRateRange, PageRequest.of(page, size)
                    )
                )
        }

    suspend fun getQuizzesByWriterId(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByWriterId(it))
        }

    suspend fun getQuizzesByQuestionContains(request: ServerRequest): ServerResponse =
        request.queryParamOrNull("question")!!.let {
            ServerResponse.ok().bodyAndAwait(quizService.getQuizzesByQuestionContains(it))
        }

    suspend fun getMarkedQuizzes(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyAndAwait(quizService.getMarkedQuizzes(it))
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

    suspend fun markQuiz(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val userId = awaitAuthentication().id

            ServerResponse.ok().bodyValueAndAwait(quizService.markQuiz(id, userId))
        }
}