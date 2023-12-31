package com.quizit.quiz.router

import com.quizit.quiz.global.annotation.Router
import com.quizit.quiz.global.util.logFilter
import com.quizit.quiz.handler.CourseHandler
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.router

@Router
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
            filter(::logFilter)
        }
}