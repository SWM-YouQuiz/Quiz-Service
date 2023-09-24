package com.quizit.quiz.router

import com.quizit.quiz.handler.ChapterHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ChapterRouter {
    @Bean
    fun chapterRoutes(handler: ChapterHandler): RouterFunction<ServerResponse> =
        router {
            "/chapter".nest {
                GET("/{id}", handler::getChapterById)
                GET("/course/{id}", handler::getChaptersByCourseId)
            }
            "/admin/chapter".nest {
                POST("", handler::createChapter)
                PUT("/{id}", handler::updateChapterById)
                DELETE("/{id}", handler::deleteChapterById)
            }
        }
}