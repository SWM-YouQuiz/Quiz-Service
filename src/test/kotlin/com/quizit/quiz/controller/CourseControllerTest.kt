package com.quizit.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCourseResponse
import com.quizit.quiz.fixture.createCreateCourseRequest
import com.quizit.quiz.fixture.createUpdateCourseByIdRequest
import com.quizit.quiz.handler.CourseHandler
import com.quizit.quiz.router.CourseRouter
import com.quizit.quiz.service.CourseService
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

@WebFluxTest(CourseRouter::class, CourseHandler::class)
class CourseControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var courseService: CourseService

    private val createCourseRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val updateCourseByIdRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val courseResponseFields = listOf(
        "id" desc "식별자",
        "title" desc "제목",
        "image" desc "이미지"
    )

    private val courseResponsesFields = courseResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("getCourses()는") {
            context("코스들이 존재하는 경우") {
                coEvery { courseService.getCourses() } returns flowOf(createCourseResponse())

                it("상태 코드 200과 courseResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/course")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(courseResponsesFields)
                            )
                        )
                }
            }
        }

        describe("createCourse()는") {
            context("어드민이 코스를 작성해서 제출하는 경우") {
                coEvery { courseService.createCourse(any()) } returns createCourseResponse()
                withMockAdmin()

                it("상태 코드 200과 courseResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/admin/course")
                        .bodyValue(createCreateCourseRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CourseResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 생성 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createCourseRequestFields),
                                responseFields(courseResponseFields)
                            )
                        )
                }
            }
        }

        describe("updateCourseById()는") {
            context("어드민이 코스를 수정해서 제출하는 경우") {
                coEvery { courseService.updateCourseById(any(), any()) } returns createCourseResponse()
                withMockAdmin()

                it("상태 코드 200과 courseResponse를 반환한다.") {
                    webClient
                        .put()
                        .uri("/admin/course/{id}", ID)
                        .bodyValue(createUpdateCourseByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CourseResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateCourseByIdRequestFields),
                                responseFields(courseResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteCourseById()는") {
            context("어드민이 코스를 삭제하는 경우") {
                coEvery { courseService.deleteCourseById(any()) } just runs
                withMockAdmin()

                it("상태 코드 200을 반환한다.") {
                    webClient
                        .delete()
                        .uri("/admin/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 삭제 성공(200)",
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