package com.quizit.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.quiz.dto.response.CheckAnswerResponse
import com.quizit.quiz.dto.response.QuizResponse
import com.quizit.quiz.exception.QuizNotFoundException
import com.quizit.quiz.fixture.*
import com.quizit.quiz.global.dto.ErrorResponse
import com.quizit.quiz.handler.QuizHandler
import com.quizit.quiz.router.QuizRouter
import com.quizit.quiz.service.QuizService
import com.quizit.quiz.util.*
import io.mockk.coEvery
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters

@WebFluxTest(QuizRouter::class, QuizHandler::class)
class QuizControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var quizService: QuizService

    private val createQuizRequestFields = listOf(
        "question" desc "지문",
        "answer" desc "정답",
        "solution" desc "풀이",
        "chapterId" desc "챕터 식별자",
        "options" desc "선지"
    )

    private val updateQuizByIdRequestFields = listOf(
        "question" desc "지문",
        "answer" desc "정답",
        "solution" desc "풀이",
        "chapterId" desc "챕터 식별자",
        "options" desc "선지",
    )

    private val checkAnswerRequestFields = listOf(
        "answer" desc "정답"
    )

    private val quizResponseFields = listOf(
        "id" desc "식별자",
        "question" desc "지문",
        "writerId" desc "작성자 식별자",
        "chapterId" desc "챕터 식별자",
        "answerRate" desc "정답률",
        "options" desc "선지",
        "correctCount" desc "정답 횟수",
        "incorrectCount" desc "오답 횟수",
        "likedUserIds" desc "좋아요한 유저 리스트",
        "createdDate" desc "생성 날짜",
    )

    private val quizResponsesFields = quizResponseFields.map { "[].${it.path}" desc it.description as String }

    private val checkAnswerResponseFields = listOf(
        "answer" desc "정답",
        "solution" desc "해설"
    )

    init {
        describe("getQuizById()는") {
            context("퀴즈가 존재하는 경우") {
                coEvery { quizService.getQuizById(any()) } returns createQuizResponse()

                it("상태 코드 200과 quizResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(QuizResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 퀴즈 단일 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(quizResponseFields)
                            )
                        )
                }
            }
        }

        describe("getQuizzesByChapterIdAndAnswerRateRange()는") {
            context("챕터와 각각의 챕터에 속하는 퀴즈들이 존재하는 경우") {
                flowOf(createQuizResponse()).let {
                    coEvery { quizService.getQuizzesByChapterIdAndAnswerRateRange(any(), any(), any()) } returns it
                }

                it("상태 코드 200과 quizResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/chapter/{id}?page={page}&size={size}&range={range}", ID, 0, 1, "0,1")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 식별자를 통한 퀴즈 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                queryParameters(
                                    "page" paramDesc "페이지 번호",
                                    "size" paramDesc "페이지 크기",
                                    "range" paramDesc "정답률 범위"
                                ),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }

        describe("getQuizzesByWriterId()는") {
            context("유저가 작성한 퀴즈가 존재하는 경우") {
                coEvery { quizService.getQuizzesByWriterId(any()) } returns flowOf(createQuizResponse())

                it("상태 코드 200과 quizResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/writer/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저가 작성한 퀴즈 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "유저 식별자"),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }

        describe("getQuizzesLikedQuiz()는") {
            context("유저가 좋아요한 퀴즈가 존재하는 경우") {
                coEvery { quizService.getQuizzesLikedQuiz(any()) } returns flowOf(createQuizResponse())
                withMockUser()

                it("상태 코드 200과 quizResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/liked-user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저가 좋아요한 퀴즈 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }

        describe("createQuiz()는") {
            context("유저가 퀴즈를 작성해서 제출하는 경우") {
                coEvery { quizService.createQuiz(any(), any()) } returns createQuizResponse()
                withMockUser()

                it("상태 코드 200과 quizResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/quiz")
                        .bodyValue(createCreateQuizRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(QuizResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "퀴즈 생성 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createQuizRequestFields),
                                responseFields(quizResponseFields)
                            )
                        )
                }
            }
        }

        describe("updateQuizById()는") {
            context("유저가 퀴즈를 수정해서 제출하는 경우") {
                coEvery { quizService.updateQuizById(any(), any(), any()) } returns createQuizResponse()
                withMockUser()

                it("상태 코드 200과 quizResponse를 반환한다.") {
                    webClient
                        .put()
                        .uri("/quiz/{id}", ID)
                        .bodyValue(createUpdateQuizByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(QuizResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "퀴즈 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateQuizByIdRequestFields),
                                responseFields(quizResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteQuizById()는") {
            context("유저가 퀴즈를 삭제하는 경우") {
                coEvery { quizService.deleteQuizById(any(), any()) } just runs
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/quiz/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "퀴즈 삭제 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                            )
                        )
                }
            }
        }

        describe("checkQuiz()는") {
            context("주어진 퀴즈 식별자에 대한 퀴즈가 존재하는 경우") {
                coEvery { quizService.checkAnswer(any(), any(), any()) } returns createCheckAnswerResponse()
                withMockUser()

                it("상태 코드 200과 정답 여부가 담긴 checkAnswerResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/quiz/{id}/check", ID)
                        .bodyValue(createCheckAnswerRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CheckAnswerResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "정답 확인 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(checkAnswerRequestFields),
                                responseFields(checkAnswerResponseFields)
                            )
                        )
                }
            }

            context("주어진 퀴즈 식별자에 대한 퀴즈가 존재하지 않는 경우") {
                coEvery { quizService.checkAnswer(any(), any(), any()) } throws QuizNotFoundException()
                withMockUser()

                it("상태 코드 404과 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/quiz/{id}/check", ID)
                        .bodyValue(createCheckAnswerRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "정답 확인 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(checkAnswerRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("likeQuiz()는") {
            context("주어진 퀴즈 식별자에 대한 퀴즈가 존재하는 경우") {
                coEvery { quizService.likeQuiz(any(), any()) } just runs
                withMockUser()

                it("상태 코드 200과 정답 여부가 담긴 checkAnswerResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/{id}/like", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CheckAnswerResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "퀴즈 좋아요 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                            )
                        )
                }
            }

            context("주어진 퀴즈 식별자에 대한 퀴즈가 존재하지 않는 경우") {
                coEvery { quizService.likeQuiz(any(), any()) } throws QuizNotFoundException()
                withMockUser()

                it("상태 코드 404과 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/{id}/like", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "퀴즈 좋아요 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }
    }
}