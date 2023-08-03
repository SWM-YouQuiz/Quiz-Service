package com.youquiz.quiz.repository

import com.youquiz.quiz.domain.Course
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CoroutineCrudRepository<Course, String>