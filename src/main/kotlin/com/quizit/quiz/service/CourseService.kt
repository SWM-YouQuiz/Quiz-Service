package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.domain.Course
import com.quizit.quiz.dto.request.CreateCourseRequest
import com.quizit.quiz.dto.request.UpdateCourseByIdRequest
import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.dto.response.GetProgressByIdResponse
import com.quizit.quiz.exception.CourseNotFoundException
import com.quizit.quiz.global.util.component1
import com.quizit.quiz.global.util.component2
import com.quizit.quiz.repository.CourseRepository
import com.quizit.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val quizRepository: QuizRepository,
    private val userClient: UserClient
) {
    fun getCourseById(id: String): Mono<CourseResponse> =
        courseRepository.findById(id)
            .switchIfEmpty(Mono.error(CourseNotFoundException()))
            .map { CourseResponse(it) }

    fun getCoursesByCurriculumId(chapterId: String): Flux<CourseResponse> =
        courseRepository.findAllByCurriculumId(chapterId)
            .map { CourseResponse(it) }

    fun getProgressById(id: String, userId: String): Mono<GetProgressByIdResponse> =
        quizRepository.findAllByCourseId(id)
            .map { it.id!! }
            .cache()
            .subscribeOn(Schedulers.boundedElastic())
            .run {
                userClient.getUserById(userId)
                    .subscribeOn(Schedulers.boundedElastic())
                    .map { it.correctQuizIds + it.incorrectQuizIds }
                    .flatMapMany { quizzes -> map { quiz -> quiz in quizzes } }
                    .filter { it }
                    .count()
                    .zipWith(count())
                    .map { (solved, total) ->
                        GetProgressByIdResponse(
                            total = total,
                            solved = solved
                        )
                    }
            }

    fun createCourse(request: CreateCourseRequest): Mono<CourseResponse> =
        with(request) {
            courseRepository.save(
                Course(
                    title = title,
                    image = image,
                    curriculumId = curriculumId
                )
            ).map { CourseResponse(it) }
        }

    fun updateCourseById(id: String, request: UpdateCourseByIdRequest): Mono<CourseResponse> =
        courseRepository.findById(id)
            .switchIfEmpty(Mono.error(CourseNotFoundException()))
            .map { request.run { it.update(title, image, curriculumId) } }
            .flatMap { courseRepository.save(it) }
            .map { CourseResponse(it) }

    fun deleteCourseById(id: String): Mono<Void> =
        courseRepository.findById(id)
            .switchIfEmpty(Mono.error(CourseNotFoundException()))
            .flatMap { courseRepository.deleteById(id) }
}