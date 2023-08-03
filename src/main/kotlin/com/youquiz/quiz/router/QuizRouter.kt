package com.youquiz.quiz.router

import com.youquiz.quiz.handler.QuizHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class QuizRouter {
    @Bean
    fun quizRoutes(handler: QuizHandler): RouterFunction<ServerResponse> =
        coRouter {
            "/api/quiz".nest {
                GET("/chapter/{id}", handler::getQuizzesByChapterId)
                GET("/writer/{id}", handler::getQuizzesByWriterId)
                GET("/liked", handler::getQuizzesLikedQuiz)
                POST("/check", handler::checkAnswer)
                POST("", handler::createQuiz)
            }
        }
}