package com.youquiz.quiz.service

import com.youquiz.quiz.dto.CourseResponse
import com.youquiz.quiz.dto.CreateCourseRequest
import com.youquiz.quiz.dto.UpdateCourseByIdRequest
import com.youquiz.quiz.exception.CourseNotFoundException
import com.youquiz.quiz.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository
) {
    fun getCourses(): Flow<CourseResponse> =
        courseRepository.findAll()
            .map { CourseResponse(it) }

    suspend fun createCourse(request: CreateCourseRequest): CourseResponse =
        with(request) {
            CourseResponse(courseRepository.save(toEntity()))
        }

    suspend fun updateCourseById(id: String, request: UpdateCourseByIdRequest): CourseResponse =
        with(request) {
            courseRepository.findById(id) ?: throw CourseNotFoundException()
            CourseResponse(courseRepository.save(toEntity(id)))
        }

    suspend fun deleteCourseById(id: String) {
        courseRepository.deleteById(id)
    }
}