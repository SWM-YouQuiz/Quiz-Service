package com.youquiz.quiz.service

import com.youquiz.quiz.adapter.client.UserClient
import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.domain.User
import com.youquiz.quiz.dto.*
import com.youquiz.quiz.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val userClient: UserClient,
    private val userProducer: UserProducer
) {
    fun findAllByChapterId(chapterId: String): Flow<QuizResponse> =
        quizRepository.findAllByChapterId(chapterId)
            .map { QuizResponse(it) }

    fun findAllByWriterId(writerId: String): Flow<QuizResponse> =
        quizRepository.findAllByWriterId(writerId)
            .map { QuizResponse(it) }

    suspend fun findAllLikedQuiz(userId: String): Flow<QuizResponse> =
        quizRepository.findAllByIdIn(userClient.findById(userId).likedQuizIds.toList())
            .map { QuizResponse(it) }

    suspend fun createQuiz(userId: String, request: CreateQuizRequest): QuizResponse =
        with(request) {
            quizRepository.save(
                Quiz(
                    question = question,
                    answer = answer,
                    solution = solution,
                    writerId = userId,
                    chapterId = chapterId,
                    options = options,
                    answerRate = 0.0,
                    correctCount = 0,
                    incorrectCount = 0,
                )
            ).let {
                QuizResponse(it)
            }
        }

    suspend fun checkAnswer(userId: Long, request: CheckAnswerRequest): CheckAnswerResponse {
        val quiz = quizRepository.findById(request.quizId)!!

        return quiz.let {
            if (request.answer == it.answer) {
                userProducer.correctAnswer(userId)
                it.correctAnswer()
                CheckAnswerResponse(true)
            } else {
                userProducer.incorrectAnswer(userId)
                it.incorrectAnswer()
                CheckAnswerResponse(false)
            }.apply {
                quizRepository.save(it)
            }
        }
    }
}