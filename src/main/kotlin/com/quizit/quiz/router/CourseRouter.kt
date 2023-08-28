package com.quizit.quiz.router

import com.quizit.quiz.handler.CourseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CourseRouter {
    @Bean
    fun courseRoutes(handler: CourseHandler) =
        coRouter {
            "/course".nest {
                GET("/curriculum/{id}", handler::getCoursesByCurriculumId)
            }
            "/admin/course".nest {
                POST("", handler::createCourse)
                PUT("/{id}", handler::updateCourseById)
                DELETE("/{id}", handler::deleteCourseById)
            }
        }
}