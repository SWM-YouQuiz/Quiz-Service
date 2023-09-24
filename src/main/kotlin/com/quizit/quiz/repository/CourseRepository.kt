package com.quizit.quiz.repository

import com.quizit.quiz.domain.Course
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface CourseRepository : ReactiveMongoRepository<Course, String> {
    fun findAllByCurriculumId(curriculumId: String): Flux<Course>
}