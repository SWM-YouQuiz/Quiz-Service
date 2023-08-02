package com.youquiz.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.youquiz.quiz.config.SecurityTestConfiguration
import com.youquiz.quiz.dto.ChapterResponse
import com.youquiz.quiz.fixture.ID
import com.youquiz.quiz.fixture.createChapterResponse
import com.youquiz.quiz.fixture.createCreateChapterRequest
import com.youquiz.quiz.fixture.createUpdateChapterRequest
import com.youquiz.quiz.handler.ChapterHandler
import com.youquiz.quiz.router.ChapterRouter
import com.youquiz.quiz.service.ChapterService
import com.youquiz.quiz.util.*
import io.mockk.coEvery
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.asFlow
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
@WebFluxTest(ChapterRouter::class, ChapterHandler::class)
class ChapterControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var chapterService: ChapterService

    private val createChapterRequestFields = listOf(
        "description" desc "설명",
        "courseId" desc "코스 식별자"
    )

    private val updateChapterRequestFields = listOf(
        "description" desc "설명",
    )

    private val chapterResponseFields = listOf(
        "id" desc "식별자",
        "description" desc "설명",
        "courseId" desc "코스 식별자"
    )

    private val chapterResponsesFields = chapterResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("findAllByCourseId()는") {
            context("코스와 각각의 코스에 속하는 챕터들이 존재하는 경우") {
                coEvery { chapterService.findAllByCourseId(any()) } returns List(3) { createChapterResponse() }.asFlow()
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
                                "코스 내 챕터 조회 성공(200)",
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
                        .uri("/chapter")
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

        describe("updateChapter()는") {
            context("어드민이 챕터를 수정해서 제출하는 경우") {
                coEvery { chapterService.updateChapter(any(), any()) } returns createChapterResponse()
                withMockAdmin()

                it("상태 코드 200과 chapterResponse를 반환한다.") {
                    webClient
                        .put()
                        .uri("/chapter/{id}", ID)
                        .bodyValue(createUpdateChapterRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(ChapterResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "챕터 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateChapterRequestFields),
                                responseFields(chapterResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteChapter()는") {
            context("어드민이 챕터를 삭제하는 경우") {
                coEvery { chapterService.deleteChapter(any()) } just runs
                withMockAdmin()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/chapter/{id}", ID)
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