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
            "/quiz".nest {
                GET("/chapter/{id}", handler::findAllByChapterId)
                GET("/writer/{id}", handler::findAllByWriterId)
                GET("/liked", handler::findAllLikedQuiz)
                POST("/check", handler::checkAnswer)
                POST("", handler::createQuiz)
            }
        }
}