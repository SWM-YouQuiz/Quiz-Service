package com.quizit.quiz.service

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.exception.ChapterNotFoundException
import com.quizit.quiz.repository.ChapterRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChapterService(
    private val chapterRepository: ChapterRepository
) {
    fun getChapterById(id: String): Mono<ChapterResponse> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .map { ChapterResponse(it) }

    fun getChaptersByCourseId(courseId: String): Flux<ChapterResponse> =
        chapterRepository.findAllByCourseId(courseId)
            .map { ChapterResponse(it) }

    fun createChapter(request: CreateChapterRequest): Mono<ChapterResponse> =
        with(request) {
            chapterRepository.save(
                Chapter(
                    description = description,
                    document = document,
                    courseId = courseId
                )
            ).map { ChapterResponse(it) }
        }

    fun updateChapterById(id: String, request: UpdateChapterByIdRequest): Mono<ChapterResponse> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .map { request.run { it.update(description, document, courseId) } }
            .flatMap { chapterRepository.save(it) }
            .map { ChapterResponse(it) }

    fun deleteChapterById(id: String): Mono<Void> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .flatMap { chapterRepository.deleteById(id) }
}