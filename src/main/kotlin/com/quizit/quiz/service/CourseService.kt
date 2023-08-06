package com.quizit.quiz.service

import com.quizit.quiz.domain.Course
import com.quizit.quiz.dto.request.CreateCourseRequest
import com.quizit.quiz.dto.request.UpdateCourseByIdRequest
import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.exception.CourseNotFoundException
import com.quizit.quiz.repository.CourseRepository
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
            courseRepository.save(
                Course(
                    title = title,
                    image = image
                )
            ).let { CourseResponse(it) }
        }

    suspend fun updateCourseById(id: String, request: UpdateCourseByIdRequest): CourseResponse =
        with(request) {
            courseRepository.findById(id) ?: throw CourseNotFoundException()
            courseRepository.save(
                Course(
                    id = id,
                    title = title,
                    image = image
                )
            ).let { CourseResponse(it) }
        }

    suspend fun deleteCourseById(id: String) {
        courseRepository.deleteById(id)
    }
}