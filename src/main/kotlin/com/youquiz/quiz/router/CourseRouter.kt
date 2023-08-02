package com.youquiz.quiz.router

import com.youquiz.quiz.handler.CourseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CourseRouter {
    @Bean
    fun courseRoutes(handler: CourseHandler) =
        coRouter {
            "/api/course".nest {
                GET("", handler::getCourses)
            }
            "/api/admin/course".nest {
                POST("", handler::createCourse)
                PUT("/{id}", handler::updateCourseById)
                DELETE("/{id}", handler::deleteCourseById)
            }
        }
}