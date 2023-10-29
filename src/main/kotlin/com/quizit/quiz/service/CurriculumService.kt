package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.domain.Curriculum
import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.dto.response.GetProgressByIdResponse
import com.quizit.quiz.exception.CurriculumNotFoundException
import com.quizit.quiz.global.util.component1
import com.quizit.quiz.global.util.component2
import com.quizit.quiz.repository.CurriculumRepository
import com.quizit.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CurriculumService(
    private val curriculumRepository: CurriculumRepository,
    private val quizRepository: QuizRepository,
    private val userClient: UserClient
) {
    fun getCurriculumById(id: String): Mono<CurriculumResponse> =
        curriculumRepository.findById(id)
            .map { CurriculumResponse(it) }

    fun getCurriculums(): Flux<CurriculumResponse> =
        curriculumRepository.findAll()
            .map { CurriculumResponse(it) }

    fun getProgressById(id: String, userId: String): Mono<GetProgressByIdResponse> =
        quizRepository.findAllByCurriculumId(id)
            .map { it.id!! }
            .collectList()
            .run {
                zipWith(userClient.getUserById(userId))
                    .map { (quizIds, user) ->
                        Pair(
                            (user.correctQuizIds + user.incorrectQuizIds).count { it in quizIds }, quizIds.count()
                        )
                    }
                    .map { (solved, total) ->
                        GetProgressByIdResponse(
                            total = total,
                            solved = solved
                        )
                    }
            }

    fun createCurriculum(request: CreateCurriculumRequest): Mono<CurriculumResponse> =
        with(request) {
            curriculumRepository.save(
                Curriculum(
                    title = title,
                    image = image
                )
            ).map { CurriculumResponse(it) }
        }

    fun updateCurriculumById(id: String, request: UpdateCurriculumByIdRequest): Mono<CurriculumResponse> =
        curriculumRepository.findById(id)
            .switchIfEmpty(Mono.error(CurriculumNotFoundException()))
            .map { request.run { it.update(title, image) } }
            .flatMap { curriculumRepository.save(it) }
            .map { CurriculumResponse(it) }

    fun deleteCurriculumById(id: String): Mono<Void> =
        curriculumRepository.findById(id)
            .switchIfEmpty(Mono.error(CurriculumNotFoundException()))
            .flatMap { curriculumRepository.deleteById(id) }
}