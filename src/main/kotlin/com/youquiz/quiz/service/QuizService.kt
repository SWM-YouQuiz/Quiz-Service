package com.youquiz.quiz.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.youquiz.quiz.adapter.client.UserClient
import com.youquiz.quiz.adapter.producer.UserProducer
import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.dto.event.CheckAnswerEvent
import com.youquiz.quiz.dto.event.LikeQuizEvent
import com.youquiz.quiz.dto.request.CheckAnswerRequest
import com.youquiz.quiz.dto.request.CreateQuizRequest
import com.youquiz.quiz.dto.request.UpdateQuizByIdRequest
import com.youquiz.quiz.dto.response.CheckAnswerResponse
import com.youquiz.quiz.dto.response.QuizResponse
import com.youquiz.quiz.exception.PermissionDeniedException
import com.youquiz.quiz.exception.QuizNotFoundException
import com.youquiz.quiz.global.config.isAdmin
import com.youquiz.quiz.repository.QuizRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val userClient: UserClient,
    private val userProducer: UserProducer
) {
    fun getQuizzesByChapterId(chapterId: String): Flow<QuizResponse> =
        quizRepository.findAllByChapterId(chapterId)
            .map { QuizResponse(it) }

    fun getQuizzesByWriterId(writerId: String): Flow<QuizResponse> =
        quizRepository.findAllByWriterId(writerId)
            .map { QuizResponse(it) }

    suspend fun getQuizzesLikedQuiz(userId: String): Flow<QuizResponse> =
        quizRepository.findAllByIdIn(userClient.getUserById(userId).likedQuizIds.toList())
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
                    likedUserIds = mutableSetOf()
                )
            ).let { QuizResponse(it) }
        }

    suspend fun updateQuizById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateQuizByIdRequest
    ): QuizResponse =
        with(request) {
            quizRepository.findById(id)?.let {
                if ((authentication.id == it.writerId) or authentication.isAdmin()) {
                    quizRepository.save(
                        Quiz(
                            question = question,
                            answer = answer,
                            solution = solution,
                            writerId = it.writerId,
                            chapterId = chapterId,
                            options = options,
                            answerRate = it.answerRate,
                            correctCount = it.correctCount,
                            incorrectCount = it.incorrectCount,
                            likedUserIds = mutableSetOf()
                        )
                    )
                } else throw PermissionDeniedException()
            }?.let { QuizResponse(it) } ?: throw QuizNotFoundException()
        }

    suspend fun deleteQuizById(id: String, authentication: DefaultJwtAuthentication) {
        quizRepository.findById(id)?.let {
            if ((authentication.id == it.writerId) or authentication.isAdmin()) {
                quizRepository.deleteById(id)
            } else throw PermissionDeniedException()
        } ?: throw QuizNotFoundException()
    }

    suspend fun checkAnswer(userId: String, request: CheckAnswerRequest): CheckAnswerResponse = coroutineScope {
        val quizDeferred = async { quizRepository.findById(request.quizId) ?: throw QuizNotFoundException() }
        val findUserByIdResponseDeferred = async { userClient.getUserById(userId) }
        val quiz = quizDeferred.await()
        val findUserByIdResponse = findUserByIdResponseDeferred.await()
        val correctQuizIds = findUserByIdResponse.correctQuizIds
        val incorrectQuizIds = findUserByIdResponse.incorrectQuizIds

        quiz.let {
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
            }.apply { quizRepository.save(it) }
        }
    }

    suspend fun likeQuiz(id: String, userId: String) {
        quizRepository.findById(id)?.run {
            if (userId in likedUserIds) {
                unlike(userId)
                LikeEvent(
                    userId = userId,
                    quizId = id,
                    isLike = false
                )
            } else {
                like(userId)
                LikeEvent(
                    userId = userId,
                    quizId = id,
                    isLike = true
                )
            }.let {
                quizRepository.save(this)
                userProducer.likeQuiz(it)
            }
        } ?: throw QuizNotFoundException()

    }
}