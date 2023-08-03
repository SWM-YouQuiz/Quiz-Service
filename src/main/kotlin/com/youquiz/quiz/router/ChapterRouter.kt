package com.youquiz.quiz.router

import com.youquiz.quiz.handler.ChapterHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class ChapterRouter {
    @Bean
    fun chapterRoutes(handler: ChapterHandler): RouterFunction<ServerResponse> =
        coRouter {
            "/api/chapter".nest {
                GET("/course/{id}", handler::findAllByCourseId)
            }
            "/api/admin/chapter".nest {
                POST("", handler::createChapter)
                PUT("/{id}", handler::updateChapter)
                DELETE("/{id}", handler::deleteChapter)
            }
        }
}