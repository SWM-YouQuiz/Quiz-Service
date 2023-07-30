package com.youquiz.quiz.service

import com.youquiz.quiz.adapter.client.UserClient
import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.dto.CheckAnswerRequest
import com.youquiz.quiz.dto.CheckAnswerResponse
import com.youquiz.quiz.dto.CreateQuizRequest
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.event.CorrectAnswerEvent
import com.youquiz.quiz.event.IncorrectAnswerEvent
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

    suspend fun checkAnswer(userId: String, request: CheckAnswerRequest): CheckAnswerResponse {
        val quiz = quizRepository.findById(request.quizId)!!
        val findUserByIdResponse = userClient.findById(userId)
        val correctQuizIds = findUserByIdResponse.correctQuizIds
        val incorrectQuizIds = findUserByIdResponse.incorrectQuizIds

        return quiz.let {
            if (request.answer == it.answer) {
                if ((it.id!! !in correctQuizIds) and (it.id!! !in incorrectQuizIds)) {
                    userProducer.correctAnswer(
                        CorrectAnswerEvent(
                            userId = userId,
                            quizId = it.id!!
                        )
                    )
                    it.correctAnswer()
                }
                CheckAnswerResponse(true)
            } else {
                if ((it.id!! !in correctQuizIds) and (it.id!! !in incorrectQuizIds)) {
                    userProducer.incorrectAnswer(
                        IncorrectAnswerEvent(
                            userId = userId,
                            quizId = it.id!!
                        )
                    )
                    it.incorrectAnswer()
                }
                CheckAnswerResponse(false)
            }.apply {
                quizRepository.save(it)
            }
        }
    }
}