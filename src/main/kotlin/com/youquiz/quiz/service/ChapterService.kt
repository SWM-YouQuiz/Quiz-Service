package com.youquiz.quiz.service

import com.youquiz.quiz.domain.Chapter
import com.youquiz.quiz.dto.request.CreateChapterRequest
import com.youquiz.quiz.dto.request.UpdateChapterByIdRequest
import com.youquiz.quiz.dto.response.ChapterResponse
import com.youquiz.quiz.exception.ChapterNotFoundException
import com.youquiz.quiz.repository.ChapterRepository
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
            chapterRepository.findById(id)?.let {
                chapterRepository.save(
                    Chapter(
                        id = id,
                        description = description,
                        courseId = it.courseId
                    )
                )
            }?.let {
                ChapterResponse(it)
            } ?: throw ChapterNotFoundException()
        }

    suspend fun deleteChapterById(id: String) {
        chapterRepository.deleteById(id)
    }
}