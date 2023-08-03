package com.youquiz.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.youquiz.quiz.dto.ChapterResponse
import com.youquiz.quiz.fixture.ID
import com.youquiz.quiz.fixture.createChapterResponse
import com.youquiz.quiz.fixture.createCreateChapterRequest
import com.youquiz.quiz.fixture.createUpdateChapterByIdRequest
import com.youquiz.quiz.handler.ChapterHandler
import com.youquiz.quiz.router.ChapterRouter
import com.youquiz.quiz.service.ChapterService
import com.youquiz.quiz.util.BaseControllerTest
import com.youquiz.quiz.util.desc
import com.youquiz.quiz.util.paramDesc
import com.youquiz.quiz.util.withMockAdmin
import io.mockk.coEvery
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters

@WebFluxTest(ChapterRouter::class, ChapterHandler::class)
class ChapterControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var chapterService: ChapterService

    private val createChapterRequestFields = listOf(
        "description" desc "설명",
        "courseId" desc "코스 식별자"
    )

    private val updateChapterByIdRequestFields = listOf(
        "description" desc "설명",
    )

    private val chapterResponseFields = listOf(
        "id" desc "식별자",
        "description" desc "설명",
        "courseId" desc "코스 식별자"
    )

    private val chapterResponsesFields = chapterResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("getChaptersByCourseId()는") {
            context("코스와 각각의 코스에 속하는 챕터들이 존재하는 경우") {
                coEvery { chapterService.getChaptersByCourseId(any()) } returns flowOf(createChapterResponse())

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
                coEvery { chapterService.createChapter(any()) } returns createChapterResponse()
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
                coEvery { chapterService.updateChapterById(any(), any()) } returns createChapterResponse()
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
        }

        describe("deleteChapterById()는") {
            context("어드민이 챕터를 삭제하는 경우") {
                coEvery { chapterService.deleteChapterById(any()) } just runs
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
        }
    }
}