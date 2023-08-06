package com.quizit.quiz.repository

import com.quizit.quiz.domain.Course
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CoroutineCrudRepository<Course, String>