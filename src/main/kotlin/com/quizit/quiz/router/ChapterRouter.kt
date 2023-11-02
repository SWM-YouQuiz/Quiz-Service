package com.quizit.quiz.router

import com.quizit.quiz.global.annotation.Router
import com.quizit.quiz.global.util.logFilter
import com.quizit.quiz.handler.ChapterHandler
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class ChapterRouter {
    @Bean
    fun chapterRoutes(handler: ChapterHandler): RouterFunction<ServerResponse> =
        router {
            "/chapter".nest {
                GET("/{id}", handler::getChapterById)
                GET("/course/{id}", handler::getChaptersByCourseId)
                GET("/{id}/progress", handler::getProgressById)
            }
            "/admin/chapter".nest {
                POST("", handler::createChapter)
                PUT("/{id}", handler::updateChapterById)
                DELETE("/{id}", handler::deleteChapterById)
            }
            filter(::logFilter)
        }
}