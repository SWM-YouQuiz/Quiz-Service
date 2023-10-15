package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.dto.response.GetProgressByIdResponse
import com.quizit.quiz.exception.ChapterNotFoundException
import com.quizit.quiz.global.util.component1
import com.quizit.quiz.global.util.component2
import com.quizit.quiz.repository.ChapterRepository
import com.quizit.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class ChapterService(
    private val chapterRepository: ChapterRepository,
    private val quizRepository: QuizRepository,
    private val userClient: UserClient
) {
    fun getChapterById(id: String): Mono<ChapterResponse> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .map { ChapterResponse(it) }

    fun getChaptersByCourseId(courseId: String): Flux<ChapterResponse> =
        chapterRepository.findAllByCourseIdOrderByIndex(courseId)
            .map { ChapterResponse(it) }

    fun getProgressById(id: String, userId: String): Mono<GetProgressByIdResponse> =
        quizRepository.findAllByChapterId(id)
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

    fun createChapter(request: CreateChapterRequest): Mono<ChapterResponse> =
        with(request) {
            chapterRepository.save(
                Chapter(
                    description = description,
                    document = document,
                    courseId = courseId,
                    index = index
                )
            ).map { ChapterResponse(it) }
        }

    fun updateChapterById(id: String, request: UpdateChapterByIdRequest): Mono<ChapterResponse> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .map { request.run { it.update(description, document, courseId, index) } }
            .flatMap { chapterRepository.save(it) }
            .map { ChapterResponse(it) }

    fun deleteChapterById(id: String): Mono<Void> =
        chapterRepository.findById(id)
            .switchIfEmpty(Mono.error(ChapterNotFoundException()))
            .flatMap { chapterRepository.deleteById(id) }
}