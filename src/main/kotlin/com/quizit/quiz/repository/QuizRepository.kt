package com.quizit.quiz.repository

import com.quizit.quiz.domain.Quiz
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface QuizRepository : ReactiveMongoRepository<Quiz, String> {
    fun findAllByChapterIdAndAnswerRateBetween(
        chapterId: String, minAnswerRate: Double, maxAnswerRate: Double, pageable: Pageable
    ): Flux<Quiz>

    fun findAllByWriterId(writerId: String): Flux<Quiz>

    fun findAllByIdIn(ids: List<String>): Flux<Quiz>

    fun findAllByQuestionContains(keyword: String): Flux<Quiz>
}