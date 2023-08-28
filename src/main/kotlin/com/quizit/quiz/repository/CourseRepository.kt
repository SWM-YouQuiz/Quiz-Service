package com.quizit.quiz.repository

import com.quizit.quiz.domain.Course
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CoroutineCrudRepository<Course, String> {
    fun findAllByCurriculumId(curriculumId: String): Flow<Course>
}