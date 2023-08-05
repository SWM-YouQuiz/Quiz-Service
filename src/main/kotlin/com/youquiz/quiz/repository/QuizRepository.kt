package com.youquiz.quiz.repository

import com.youquiz.quiz.domain.Quiz
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : CoroutineCrudRepository<Quiz, String> {
    fun findAllByChapterId(chapterId: String): Flow<Quiz>

    fun findAllByChapterId(chapterId: String, pageable: Pageable): Flow<Quiz>

    fun findAllByWriterId(writerId: String): Flow<Quiz>

    fun findAllByIdIn(ids: List<String>): Flow<Quiz>
}