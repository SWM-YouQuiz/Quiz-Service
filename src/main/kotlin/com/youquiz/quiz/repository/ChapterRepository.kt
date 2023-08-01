package com.youquiz.quiz.repository

import com.youquiz.quiz.domain.Chapter
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ChapterRepository : CoroutineCrudRepository<Chapter, String> {
    fun findAllByCourseId(courseId: String): Flow<Chapter>
}