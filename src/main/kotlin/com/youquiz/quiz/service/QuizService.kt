package com.youquiz.quiz.service

import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.dto.CheckAnswerRequest
import com.youquiz.quiz.dto.CheckAnswerResponse
import com.youquiz.quiz.dto.FindAllMarkedQuizRequest
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val userProducer: UserProducer
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

    suspend fun checkAnswer(userId: Long, request: CheckAnswerRequest): CheckAnswerResponse {
        val quiz = quizRepository.findById(request.quizId)!!

        return quiz.let {
            if (request.answer == it.answer) {
                userProducer.correctAnswer(userId)
                it.correctCount += 1
                CheckAnswerResponse(true)
            } else {
                userProducer.incorrectAnswer(userId)
                it.incorrectCount += 1
                CheckAnswerResponse(false)
            }.apply {
                it.changeAnswerRate()
                quizRepository.save(it)
            }
        }
    }
}