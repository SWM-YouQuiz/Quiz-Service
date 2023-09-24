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
import com.quizit.quiz.global.util.component1
import com.quizit.quiz.global.util.component2
import com.quizit.quiz.repository.QuizCacheRepository
import com.quizit.quiz.repository.QuizRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizCacheRepository: QuizCacheRepository,
    private val userClient: UserClient,
    private val quizProducer: QuizProducer
) {
    fun getQuizById(id: String): Mono<QuizResponse> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .map { QuizResponse(it) }

    fun getQuizzesByChapterIdAndAnswerRateRange(
        chapterId: String, answerRateRange: Set<Double>, pageable: Pageable
    ): Flux<QuizResponse> =
        with(answerRateRange) {
            quizRepository.findAllByChapterIdAndAnswerRateBetween(chapterId, min(), max(), pageable)
                .map { QuizResponse(it) }
        }

    fun getQuizzesByWriterId(writerId: String): Flux<QuizResponse> =
        quizRepository.findAllByWriterId(writerId)
            .map { QuizResponse(it) }

    fun getQuizzesByQuestionContains(keyword: String): Flux<QuizResponse> =
        quizRepository.findAllByQuestionContains(keyword)
            .map { QuizResponse(it) }

    fun getMarkedQuizzes(userId: String): Flux<QuizResponse> =
        userClient.getUserById(userId)
            .flatMapMany { quizRepository.findAllByIdIn(it.markedQuizIds.toList()) }
            .map { QuizResponse(it) }

    fun createQuiz(userId: String, request: CreateQuizRequest): Mono<QuizResponse> =
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
                    markedUserIds = hashSetOf(),
                    likedUserIds = hashSetOf(),
                    unlikedUserIds = hashSetOf()
                )
            ).flatMap {
                quizCacheRepository.save(it)
                    .thenReturn(it)
            }.map { QuizResponse(it) }
        }

    fun updateQuizById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateQuizByIdRequest
    ): Mono<QuizResponse> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .filter { (authentication.id == it.writerId) || authentication.isAdmin() }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .map { request.run { it.update(question, answer, solution, chapterId, options) } }
            .flatMap {
                Mono.zip(
                    quizRepository.save(it)
                        .subscribeOn(Schedulers.parallel()),
                    quizCacheRepository.save(it)
                        .subscribeOn(Schedulers.parallel())
                )
            }
            .map { (quiz) -> QuizResponse(quiz) }

    fun deleteQuizById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .filter { (authentication.id == it.writerId) || authentication.isAdmin() }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap {
                Mono.`when`(
                    quizRepository.deleteById(id)
                        .subscribeOn(Schedulers.parallel()),
                    quizCacheRepository.deleteById(id)
                        .subscribeOn(Schedulers.parallel())
                )
            }

    fun checkAnswer(id: String, userId: String, request: CheckAnswerRequest): Mono<CheckAnswerResponse> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .cache()
            .run {
                subscribeOn(Schedulers.parallel())
                    .zipWith(
                        userClient.getUserById(userId)
                            .subscribeOn(Schedulers.parallel())
                    )
                    .filter { (_, userResponse) -> (id !in userResponse.correctQuizIds) && (id !in userResponse.incorrectQuizIds) }
                    .flatMap { (quiz) ->
                        quiz.run {
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
                        }.thenReturn(quiz)
                    }
                    .flatMap {
                        Mono.zip(
                            quizRepository.save(it)
                                .subscribeOn(Schedulers.parallel()),
                            quizCacheRepository.save(it)
                                .subscribeOn(Schedulers.parallel())
                        )
                    }
                    .then(map {
                        CheckAnswerResponse(
                            answer = it.answer,
                            solution = it.solution
                        )
                    })
            }

    fun markQuiz(id: String, userId: String): Mono<QuizResponse> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .flatMap {
                it.run {
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
                }.thenReturn(it)
            }
            .flatMap {
                Mono.zip(
                    quizRepository.save(it)
                        .subscribeOn(Schedulers.parallel()),
                    quizCacheRepository.save(it)
                        .subscribeOn(Schedulers.parallel())
                )
            }
            .map { (quiz) -> QuizResponse(quiz) }

    fun evaluateQuiz(id: String, userId: String, isLike: Boolean): Mono<QuizResponse> =
        quizCacheRepository.findById(id)
            .switchIfEmpty(Mono.error(QuizNotFoundException()))
            .map {
                it.apply {
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
                }
            }
            .flatMap {
                Mono.zip(
                    quizRepository.save(it)
                        .subscribeOn(Schedulers.parallel()),
                    quizCacheRepository.save(it)
                        .subscribeOn(Schedulers.parallel())
                )
            }
            .map { (quiz) -> QuizResponse(quiz) }
}