package com.quizit.quiz.repository

import com.quizit.quiz.domain.Quiz
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : CoroutineCrudRepository<Quiz, String> {
    fun findAllByChapterIdAndAnswerRateBetween(
        chapterId: String, minAnswerRate: Double, maxAnswerRate: Double, pageable: Pageable
    ): Flow<Quiz>

    fun findAllByWriterId(writerId: String): Flow<Quiz>

    fun findAllByIdIn(ids: List<String>): Flow<Quiz>

    fun findAllByQuestionContains(keyword: String): Flow<Quiz>
}