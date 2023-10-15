package com.quizit.quiz.repository

import com.quizit.quiz.domain.Chapter
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ChapterRepository : ReactiveMongoRepository<Chapter, String> {
    fun findAllByCourseIdOrderByIndex(courseId: String): Flux<Chapter>
}