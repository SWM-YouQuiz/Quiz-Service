package com.quizit.quiz.service

import com.quizit.quiz.domain.Curriculum
import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.exception.CurriculumNotFoundException
import com.quizit.quiz.repository.CurriculumRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CurriculumService(
    private val curriculumRepository: CurriculumRepository
) {
    fun getCurriculumById(id: String): Mono<CurriculumResponse> =
        curriculumRepository.findById(id)
            .map { CurriculumResponse(it) }

    fun getCurriculums(): Flux<CurriculumResponse> =
        curriculumRepository.findAll()
            .map { CurriculumResponse(it) }

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