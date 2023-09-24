package com.quizit.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.exception.ChapterNotFoundException
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createChapterResponse
import com.quizit.quiz.fixture.createCreateChapterRequest
import com.quizit.quiz.fixture.createUpdateChapterByIdRequest
import com.quizit.quiz.global.dto.ErrorResponse
import com.quizit.quiz.handler.ChapterHandler
import com.quizit.quiz.router.ChapterRouter
import com.quizit.quiz.service.ChapterService
import com.quizit.quiz.util.*
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(ChapterRouter::class, ChapterHandler::class)
class ChapterControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var chapterService: ChapterService

    private val createChapterRequestFields = listOf(
        "description" desc "설명",
        "document" desc "공식 문서",
        "courseId" desc "코스 식별자"
    )

    private val updateChapterByIdRequestFields = listOf(
        "description" desc "설명",
        "document" desc "공식 문서",
        "courseId" desc "코스 식별자",
    )

    private val chapterResponseFields = listOf(
        "id" desc "식별자",
        "description" desc "설명",
        "document" desc "공식 문서",
        "courseId" desc "코스 식별자"
    )

    private val chapterResponsesFields = chapterResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("getChapterById()는") {
            context("챕터가 존재하는 경우") {
                every { chapterService.getChapterById(any()) } returns Mono.just(createChapterResponse())
                withMockUser()

                it("상태 코드 200과 chapterResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/chapter/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(ChapterResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 챕터 단일 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(chapterResponseFields)
                            )
                        )
                }
            }

            context("챕터가 존재하지 않는 경우") {
                every { chapterService.getChapterById(any()) } throws ChapterNotFoundException()
                withMockUser()

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .get()
                        .uri("/chapter/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 챕터 단일 조회 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("getChaptersByCourseId()는") {
            context("코스와 각각의 코스에 속하는 챕터들이 존재하는 경우") {
                every { chapterService.getChaptersByCourseId(any()) } returns Flux.just(createChapterResponse())
                withMockUser()

                it("상태 코드 200과 chapterResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/chapter/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 식별자를 통한 챕터 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(chapterResponsesFields)
                            )
                        )
                }
            }
        }

        describe("createChapter()는") {
            context("어드민이 챕터를 작성해서 제출하는 경우") {
                every { chapterService.createChapter(any()) } returns Mono.just(createChapterResponse())
                withMockAdmin()

                it("상태 코드 200과 chapterResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/admin/chapter")
                        .bodyValue(createCreateChapterRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(ChapterResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 생성 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createChapterRequestFields),
                                responseFields(chapterResponseFields)
                            )
                        )
                }
            }
        }

        describe("updateChapterById()는") {
            context("어드민이 챕터를 수정해서 제출하는 경우") {
                every { chapterService.updateChapterById(any(), any()) } returns Mono.just(createChapterResponse())
                withMockAdmin()

                it("상태 코드 200과 chapterResponse를 반환한다.") {
                    webClient
                        .put()
                        .uri("/admin/chapter/{id}", ID)
                        .bodyValue(createUpdateChapterByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(ChapterResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateChapterByIdRequestFields),
                                responseFields(chapterResponseFields)
                            )
                        )
                }
            }

            context("챕터가 존재하지 않는 경우") {
                every { chapterService.updateChapterById(any(), any()) } throws ChapterNotFoundException()
                withMockAdmin()

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .put()
                        .uri("/admin/chapter/{id}", ID)
                        .bodyValue(createUpdateChapterByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 수정 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateChapterByIdRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteChapterById()는") {
            context("어드민이 챕터를 삭제하는 경우") {
                every { chapterService.deleteChapterById(any()) } returns Mono.empty()
                withMockAdmin()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/admin/chapter/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 삭제 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                            )
                        )
                }
            }

            context("챕터가 존재하지 않는 경우") {
                every { chapterService.deleteChapterById(any()) } throws ChapterNotFoundException()
                withMockAdmin()

                it("상태 코드 404을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/admin/chapter/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 삭제 실패(404)",
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