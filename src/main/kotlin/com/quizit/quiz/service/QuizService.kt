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
import com.quizit.quiz.repository.QuizCacheRepository
import com.quizit.quiz.repository.QuizRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizCacheRepository: QuizCacheRepository,
    private val userClient: UserClient,
    private val quizProducer: QuizProducer
) {
    suspend fun getQuizById(id: String): QuizResponse =
        quizCacheRepository.findById(id)?.let { QuizResponse(it) } ?: throw QuizNotFoundException()

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
        quizRepository.findAllByIdIn(userClient.getUserById(userId).markedQuizIds.toList())
            .map { QuizResponse(it) }

    suspend fun createQuiz(userId: String, request: CreateQuizRequest): QuizResponse =
        coroutineScope {
            with(request) {
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
                    markedUserIds = mutableSetOf(),
                    likedUserIds = mutableSetOf(),
                    unlikedUserIds = mutableSetOf()
                ).let {
                    val quizDeferred = async { quizRepository.save(it) }
                    val cacheJob = launch { quizCacheRepository.save(it) }

                    cacheJob.join()
                    quizDeferred.await()
                }
            }.let { QuizResponse(it) }
        }

    suspend fun updateQuizById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateQuizByIdRequest
    ): QuizResponse =
        coroutineScope {
            with(request) {
                quizCacheRepository.findById(id)?.let {
                    if ((authentication.id == it.writerId) || authentication.isAdmin()) {
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
                            markedUserIds = it.markedUserIds,
                            likedUserIds = it.likedUserIds,
                            unlikedUserIds = it.unlikedUserIds,
                            createdDate = it.createdDate
                        ).apply {
                            val quizJob = launch { quizRepository.save(it) }
                            val cacheJob = launch { quizCacheRepository.save(it) }

                            quizJob.join()
                            cacheJob.join()
                        }
                    } else throw PermissionDeniedException()
                }?.let { QuizResponse(it) } ?: throw QuizNotFoundException()
            }
        }

    suspend fun deleteQuizById(id: String, authentication: DefaultJwtAuthentication) =
        coroutineScope {
            quizCacheRepository.findById(id)?.let {
                if ((authentication.id == it.writerId) || authentication.isAdmin()) {
                    val quizJob = launch { quizRepository.deleteById(id) }
                    val cacheJob = launch { quizCacheRepository.deleteById(id) }

                    quizJob.join()
                    cacheJob.join()
                } else throw PermissionDeniedException()
            } ?: throw QuizNotFoundException()
        }

    suspend fun checkAnswer(id: String, userId: String, request: CheckAnswerRequest): CheckAnswerResponse =
        coroutineScope {
            val quizDeferred = async { quizCacheRepository.findById(id) ?: throw QuizNotFoundException() }
            val userResponseDeferred = async { userClient.getUserById(userId) }
            val quiz = quizDeferred.await()
            val userResponse = userResponseDeferred.await()

            quiz.apply {
                if ((id !in userResponse.correctQuizIds) && (id !in userResponse.incorrectQuizIds)) {
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
                    val quizJob = launch { quizRepository.save(this@apply) }
                    val cacheJob = launch { quizCacheRepository.save(this@apply) }

                    quizJob.join()
                    cacheJob.join()
                }
            }.run {
                CheckAnswerResponse(
                    answer = answer,
                    solution = solution
                )
            }
        }

    suspend fun markQuiz(id: String, userId: String): QuizResponse =
        coroutineScope {
            quizCacheRepository.findById(id)?.apply {
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
                val quizJob = launch { quizRepository.save(this@apply) }
                val cacheJob = launch { quizCacheRepository.save(this@apply) }

                quizJob.join()
                cacheJob.join()
            }?.let { QuizResponse(it) } ?: throw QuizNotFoundException()
        }

    suspend fun evaluateQuiz(id: String, userId: String, isLike: Boolean): QuizResponse =
        coroutineScope {
            quizCacheRepository.findById(id)?.apply {
                if (isLike) {
                    if (userId in likedUserIds) {
                        likedUserIds.remove(userId)
                    } else {
                        unlikedUserIds.remove(userId)
                        like(userId)
                    }
                } else {
                    if (userId in unlikedUserIds) {
                        unlikedUserIds.remove(userId)
                    } else {
                        likedUserIds.remove(userId)
                        unlike(userId)
                    }
                }
                val quizJob = launch { quizRepository.save(this@apply) }
                val cacheJob = launch { quizCacheRepository.save(this@apply) }

                quizJob.join()
                cacheJob.join()
            }?.let { QuizResponse(it) } ?: throw QuizNotFoundException()
        }
}