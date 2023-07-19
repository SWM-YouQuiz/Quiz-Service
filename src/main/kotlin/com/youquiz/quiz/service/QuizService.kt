package com.youquiz.quiz.service

import com.youquiz.quiz.dto.FindAllMarkedQuizRequest
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository
) {
    fun findAllByChapterId(chapterId: String): Flow<QuizResponse> =
        quizRepository.findAllByChapterId(chapterId)
            .map { QuizResponse(it) }

    fun findAllByWriterId(writerId: Long): Flow<QuizResponse> =
        quizRepository.findAllByWriterId(writerId)
            .map { QuizResponse(it) }

    fun findAllMarkedQuiz(request: FindAllMarkedQuizRequest): Flow<QuizResponse> =
        quizRepository.findAllByIdIn(request.quizIds)
            .map { QuizResponse(it) }
}