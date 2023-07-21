package com.youquiz.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.youquiz.quiz.config.SecurityTestConfiguration
import com.youquiz.quiz.dto.FindAllMarkedQuizRequest
import com.youquiz.quiz.dto.QuizResponse
import com.youquiz.quiz.fixture.ID
import com.youquiz.quiz.fixture.OBJECT_ID
import com.youquiz.quiz.fixture.createQuiz
import com.youquiz.quiz.handler.QuizHandler
import com.youquiz.quiz.router.QuizRouter
import com.youquiz.quiz.service.QuizService
import com.youquiz.quiz.util.BaseControllerTest
import com.youquiz.quiz.util.desc
import com.youquiz.quiz.util.paramDesc
import io.mockk.coEvery
import kotlinx.coroutines.flow.asFlow
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
@WebFluxTest(QuizRouter::class, QuizHandler::class)
class QuizControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var quizService: QuizService

    private val findAllMarkedQuizRequest = FindAllMarkedQuizRequest(quizIds = listOf(OBJECT_ID))

    private val quizResponse = QuizResponse(createQuiz())

    private val findAllMarkedQuizRequestFields = listOf(
        "quizIds" desc "퀴즈 식별자 목록"
    )

    private val quizResponseFields = listOf(
        "id" desc "퀴즈 식별자",
        "question" desc "지문",
        "answer" desc "정답",
        "solution" desc "풀이",
        "writer" desc "작성자",
        "chapterId" desc "챕터 식별자",
        "answerRate" desc "정답률",
        "correctCount" desc "정답 횟수",
        "incorrectCount" desc "오답 횟수",
        "createdDate" desc "생성 날짜"
    )

    private val quizResponsesFields = quizResponseFields.map { "[].${it.path}" desc it.description as String } +
            listOf(
                "[].writer.id" desc "유저 식별자",
                "[].writer.nickname" desc "닉네임"
            )

    init {
        describe("findAllByChapterId()는") {
            context("챕터와 각각의 챕터에 속하는 퀴즈들이 존재하는 경우") {
                coEvery { quizService.findAllByChapterId(any()) } returns List(3) { quizResponse }.asFlow()

                it("상태 코드 200과 quizResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/quiz/chapter/{id}", OBJECT_ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 내 퀴즈 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "챕터 식별자"),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }

        describe("findAllByWriterId()는") {
            context("유저가 작성한 퀴즈가 존재하는 경우") {
                coEvery { quizService.findAllByWriterId(any()) } returns List(3) { quizResponse }.asFlow()

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
                                "유저가 작성한 퀴즈 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "유저 식별자"),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }

        describe("findAllMarkedQuiz()는") {
            context("유저가 저장한 퀴즈가 존재하는 경우") {
                coEvery { quizService.findAllMarkedQuiz(any()) } returns List(3) { quizResponse }.asFlow()

                it("상태 코드 200과 quizResponse들을 반환한다.") {
                    webClient
                        .post()
                        .uri("/quiz/mark")
                        .bodyValue(findAllMarkedQuizRequest)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저가 저장한 퀴즈 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(findAllMarkedQuizRequestFields),
                                responseFields(quizResponsesFields)
                            )
                        )
                }
            }
        }
    }
}