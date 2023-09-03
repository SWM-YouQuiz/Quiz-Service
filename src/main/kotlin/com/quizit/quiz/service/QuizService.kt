package com.quizit.quiz.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.adapter.producer.QuizProducer
import com.quizit.quiz.domain.Quiz
import com.quizit.quiz.dto.event.CheckAnswerEvent
import com.quizit.quiz.dto.event.MarkQuizEvent
import com.quizit.quiz.dto.request.CheckAnswerRequest
import com.quizit.quiz.dto.request.CreateQuizRequest
import com.quizit.quiz.dto.request.UpdateQuizByIdRequest
import com.quizit.quiz.dto.response.CheckAnswerResponse
import com.quizit.quiz.dto.response.QuizResponse
import com.quizit.quiz.exception.PermissionDeniedException
import com.quizit.quiz.exception.QuizNotFoundException
import com.quizit.quiz.global.config.isAdmin
import com.quizit.quiz.repository.QuizRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val userClient: UserClient,
    private val quizProducer: QuizProducer
) {
    suspend fun getQuizById(id: String): QuizResponse =
        quizRepository.findById(id)?.let { QuizResponse(it) } ?: throw QuizNotFoundException()

    fun getQuizzesByChapterIdAndAnswerRateRange(
        chapterId: String, answerRateRange: Set<Double>, pageable: Pageable
    ): Flow<QuizResponse> =
        with(answerRateRange) {
            quizRepository.findAllByChapterIdAndAnswerRateBetween(chapterId, min(), max(), pageable)
                .map { QuizResponse(it) }
        }

    fun getQuizzesByWriterId(writerId: String): Flow<QuizResponse> =
        quizRepository.findAllByWriterId(writerId)
            .map { QuizResponse(it) }

    fun getQuizzesByQuestionContains(keyword: String): Flow<QuizResponse> =
        quizRepository.findAllByQuestionContains(keyword)
            .map { QuizResponse(it) }

    suspend fun getMarkedQuizzes(userId: String): Flow<QuizResponse> =
        quizRepository.findAllByIdIn(userClient.getUserById(userId).markedQuidIds.toList())
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
                    markedUserIds = mutableSetOf()
                )
            ).let { QuizResponse(it) }
        }

    suspend fun updateQuizById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateQuizByIdRequest
    ): QuizResponse =
        with(request) {
            quizRepository.findById(id)?.let {
                if ((authentication.id == it.writerId) || authentication.isAdmin()) {
                    quizRepository.save(
                        Quiz(
                            id = id,
                            question = question,
                            answer = answer,
                            solution = solution,
                            writerId = it.writerId,
                            chapterId = chapterId,
                            options = options,
                            answerRate = it.answerRate,
                            correctCount = it.correctCount,
                            incorrectCount = it.incorrectCount,
                            markedUserIds = it.markedUserIds
                        )
                    )
                } else throw PermissionDeniedException()
            }?.let { QuizResponse(it) } ?: throw QuizNotFoundException()
        }

    suspend fun deleteQuizById(id: String, authentication: DefaultJwtAuthentication) {
        quizRepository.findById(id)?.let {
            if ((authentication.id == it.writerId) || authentication.isAdmin()) {
                quizRepository.deleteById(id)
            } else throw PermissionDeniedException()
        } ?: throw QuizNotFoundException()
    }

    suspend fun checkAnswer(id: String, userId: String, request: CheckAnswerRequest): CheckAnswerResponse =
        coroutineScope {
            val quizDeferred = async { quizRepository.findById(id) ?: throw QuizNotFoundException() }
            val findUserByIdResponseDeferred = async { userClient.getUserById(userId) }
            val quiz = quizDeferred.await()
            val findUserByIdResponse = findUserByIdResponseDeferred.await()
            val correctQuizIds = findUserByIdResponse.correctQuizIds
            val incorrectQuizIds = findUserByIdResponse.incorrectQuizIds

            quiz.apply {
                if ((id !in correctQuizIds) && (id !in incorrectQuizIds)) {
                    quizProducer.checkAnswer(
                        CheckAnswerEvent(
                            userId = userId,
                            quizId = id,
                            isAnswer = (request.answer == answer)
                        ).apply {
                            if (isAnswer) {
                                correctAnswer()
                            } else {
                                incorrectAnswer()
                            }
                        }
                    )
                    quizRepository.save(this)
                }
            }.run {
                CheckAnswerResponse(
                    answer = answer,
                    solution = solution
                )
            }
        }

    suspend fun markQuiz(id: String, userId: String) {
        quizRepository.findById(id)?.run {
            quizProducer.markQuiz(
                MarkQuizEvent(
                    userId = userId,
                    quizId = id,
                    isMarked = (userId !in markedUserIds)
                ).apply {
                    if (isMarked) {
                        mark(userId)
                    } else {
                        unmark(userId)
                    }
                }
            )
            quizRepository.save(this)
        } ?: throw QuizNotFoundException()
    }
}