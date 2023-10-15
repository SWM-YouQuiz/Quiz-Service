package com.quizit.quiz.router

import com.quizit.quiz.handler.CourseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class CourseRouter {
    @Bean
    fun courseRoutes(handler: CourseHandler) =
        router {
            "/course".nest {
                GET("/{id}", handler::getCourseById)
                GET("/curriculum/{id}", handler::getCoursesByCurriculumId)
                GET("/{id}/progress", handler::getProgressById)
            }
            "/admin/course".nest {
                POST("", handler::createCourse)
                PUT("/{id}", handler::updateCourseById)
                DELETE("/{id}", handler::deleteCourseById)
            }
        }
}