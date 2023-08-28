package com.quizit.quiz.service

import com.quizit.quiz.domain.Chapter
import com.quizit.quiz.dto.request.CreateChapterRequest
import com.quizit.quiz.dto.request.UpdateChapterByIdRequest
import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.exception.ChapterNotFoundException
import com.quizit.quiz.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ChapterService(
    private val chapterRepository: ChapterRepository
) {
    fun getChaptersByCourseId(courseId: String): Flow<ChapterResponse> =
        chapterRepository.findAllByCourseId(courseId)
            .map { ChapterResponse(it) }

    suspend fun createChapter(request: CreateChapterRequest): ChapterResponse =
        with(request) {
            chapterRepository.save(
                Chapter(
                    description = description,
                    courseId = courseId
                )
            ).let { ChapterResponse(it) }
        }

    suspend fun updateChapterById(id: String, request: UpdateChapterByIdRequest): ChapterResponse =
        with(request) {
            chapterRepository.findById(id) ?: throw ChapterNotFoundException()
            chapterRepository.save(
                Chapter(
                    id = id,
                    description = description,
                    courseId = courseId
                )
            ).let { ChapterResponse(it) }
        }

    suspend fun deleteChapterById(id: String) {
        chapterRepository.deleteById(id)
    }
}