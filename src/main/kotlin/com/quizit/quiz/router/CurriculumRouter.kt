package com.quizit.quiz.router

import com.quizit.quiz.handler.CurriculumHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class CurriculumRouter {
    @Bean
    fun curriculumRoutes(handler: CurriculumHandler): RouterFunction<ServerResponse> =
        router {
            "/curriculum".nest {
                GET("", handler::getCurriculums)
                GET("/{id}", handler::getCurriculumById)
                GET("/{id}/progress", handler::getProgressById)
            }
            "/admin/curriculum".nest {
                POST("", handler::createCurriculum)
                PUT("/{id}", handler::updateCurriculumById)
                DELETE("/{id}", handler::deleteCurriculumById)
            }
        }
}