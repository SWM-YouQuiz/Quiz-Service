package com.quizit.quiz.router

import com.quizit.quiz.global.annotation.Router
import com.quizit.quiz.global.util.logFilter
import com.quizit.quiz.global.util.queryParams
import com.quizit.quiz.handler.QuizHandler
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class QuizRouter {
    @Bean
    fun quizRoutes(handler: QuizHandler): RouterFunction<ServerResponse> =
        router {
            "/quiz".nest {
                GET("/search", queryParams("question"), handler::getQuizzesByQuestionContains)
                GET("/{id}", handler::getQuizById)
                GET(
                    "/chapter/{id}",
                    queryParams("page", "size", "range"),
                    handler::getQuizzesByChapterIdAndAnswerRateRange
                )
                GET("/chapter/{id}", handler::getQuizzesByChapterId)
                GET("/course/{id}", handler::getQuizzesByCourseId)
                GET("/writer/{id}", handler::getQuizzesByWriterId)
                GET("/{id}/mark", handler::markQuiz)
                GET("/{id}/evaluate", queryParams("isLike"), handler::evaluateQuiz)
                GET("/marked-user/{id}", handler::getMarkedQuizzes)
                POST("", handler::createQuiz)
                POST("/{id}/check", handler::checkAnswer)
                POST("/{id}/evaluate", handler::evaluateQuiz)
                PUT("/{id}", handler::updateQuizById)
                DELETE("/{id}", handler::deleteQuizById)
            }
            filter(::logFilter)
        }
}