package com.quizit.quiz.service

import com.quizit.quiz.domain.Curriculum
import com.quizit.quiz.dto.request.CreateCurriculumRequest
import com.quizit.quiz.dto.request.UpdateCurriculumByIdRequest
import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.exception.CurriculumNotFoundException
import com.quizit.quiz.repository.CurriculumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class CurriculumService(
    private val curriculumRepository: CurriculumRepository
) {
    suspend fun getCurriculumById(id: String): CurriculumResponse =
        curriculumRepository.findById(id)?.let { CurriculumResponse(it) } ?: throw CurriculumNotFoundException()

    fun getCurriculums(): Flow<CurriculumResponse> =
        curriculumRepository.findAll()
            .map { CurriculumResponse(it) }

    suspend fun createCurriculum(request: CreateCurriculumRequest): CurriculumResponse =
        with(request) {
            curriculumRepository.save(
                Curriculum(
                    title = title,
                    image = image
                )
            ).let { CurriculumResponse(it) }
        }

    suspend fun updateCurriculumById(id: String, request: UpdateCurriculumByIdRequest): CurriculumResponse =
        with(request) {
            curriculumRepository.findById(id)?.let {
                curriculumRepository.save(
                    Curriculum(
                        id = id,
                        title = title,
                        image = image
                    )
                )
            }?.let {
                CurriculumResponse(it)
            } ?: throw CurriculumNotFoundException()
        }

    suspend fun deleteCurriculumById(id: String) {
        curriculumRepository.findById(id) ?: CurriculumNotFoundException()
        curriculumRepository.deleteById(id)
    }
}