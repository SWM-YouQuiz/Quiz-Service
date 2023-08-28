package com.quizit.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCreateCurriculumRequest
import com.quizit.quiz.fixture.createCurriculumResponse
import com.quizit.quiz.fixture.createUpdateCurriculumByIdRequest
import com.quizit.quiz.handler.CurriculumHandler
import com.quizit.quiz.router.CurriculumRouter
import com.quizit.quiz.service.CurriculumService
import com.quizit.quiz.util.BaseControllerTest
import com.quizit.quiz.util.desc
import com.quizit.quiz.util.paramDesc
import com.quizit.quiz.util.withMockAdmin
import io.mockk.coEvery
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters

@WebFluxTest(CurriculumRouter::class, CurriculumHandler::class)
class CurriculumControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var curriculumService: CurriculumService

    private val createCurriculumRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val updateCurriculumByIdRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val curriculumResponseFields = listOf(
        "id" desc "식별자",
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val curriculumResponsesFields =
        curriculumResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("getCurriculums()는") {
            context("커리큘럼들이 존재하는 경우") {
                coEvery { curriculumService.getCurriculums() } returns flowOf(createCurriculumResponse())

                it("상태 코드 200과 curriculumResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/curriculum")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "커리큘럼 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(curriculumResponsesFields)
                            )
                        )
                }
            }
        }

        describe("createCurriculum()는") {
            context("어드민이 챕터를 작성해서 제출하는 경우") {
                coEvery { curriculumService.createCurriculum(any()) } returns createCurriculumResponse()
                withMockAdmin()

                it("상태 코드 200과 curriculumResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/admin/curriculum")
                        .bodyValue(createCreateCurriculumRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CurriculumResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "커리큘럼 생성 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createCurriculumRequestFields),
                                responseFields(curriculumResponseFields)
                            )
                        )
                }
            }
        }

        describe("updateCurriculumById()는") {
            context("어드민이 커리큘럼을 수정해서 제출하는 경우") {
                coEvery { curriculumService.updateCurriculumById(any(), any()) } returns createCurriculumResponse()
                withMockAdmin()

                it("상태 코드 200과 curriculumResponse를 반환한다.") {
                    webClient
                        .put()
                        .uri("/admin/curriculum/{id}", ID)
                        .bodyValue(createUpdateCurriculumByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CurriculumResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "커리큘럼 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateCurriculumByIdRequestFields),
                                responseFields(curriculumResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteCurriculumById()는") {
            context("어드민이 챕터를 삭제하는 경우") {
                coEvery { curriculumService.deleteCurriculumById(any()) } just runs
                withMockAdmin()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/admin/curriculum/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "커리큘럼 삭제 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                            )
                        )
                }
            }
        }
    }
}