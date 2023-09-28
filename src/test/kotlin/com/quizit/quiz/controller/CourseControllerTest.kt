package com.quizit.quiz.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.exception.CourseNotFoundException
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCourseResponse
import com.quizit.quiz.fixture.createCreateCourseRequest
import com.quizit.quiz.fixture.createUpdateCourseByIdRequest
import com.quizit.quiz.global.dto.ErrorResponse
import com.quizit.quiz.handler.CourseHandler
import com.quizit.quiz.router.CourseRouter
import com.quizit.quiz.service.CourseService
import com.quizit.quiz.util.*
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(CourseRouter::class, CourseHandler::class)
class CourseControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var courseService: CourseService

    private val createCourseRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지",
        "curriculumId" desc "커리큘럼 식별자"
    )

    private val updateCourseByIdRequestFields = listOf(
        "title" desc "제목",
        "image" desc "이미지",
        "curriculumId" desc "커리큘럼 식별자"
    )

    private val courseResponseFields = listOf(
        "id" desc "식별자",
        "title" desc "제목",
        "image" desc "이미지",
        "curriculumId" desc "커리큘럼 식별자"
    )

    private val courseResponsesFields = courseResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("getCourseById()는") {
            context("코스가 존재하는 경우") {
                every { courseService.getCourseById(any()) } returns Mono.just(createCourseResponse())
                withMockUser()

                it("상태 코드 200과 courseResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(CourseResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 코스 단일 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(courseResponseFields)
                            )
                        )
                }
            }

            context("코스가 존재하지 않는 경우") {
                every { courseService.getCourseById(any()) } throws CourseNotFoundException()
                withMockUser()

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .get()
                        .uri("/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 코스 단일 조회 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("getCoursesByCurriculumId()는") {
            context("커리큘럼과 각각의 커리큘럼에 속하는 코스들이 존재하는 경우") {
                every { courseService.getCoursesByCurriculumId(any()) } returns Flux.just(createCourseResponse())
                withMockUser()

                it("상태 코드 200과 courseResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/course/curriculum/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "커리큘럼 식별자를 통한 코스 전체 조회 성공(200)",
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
                every { courseService.createCourse(any()) } returns Mono.just(createCourseResponse())
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
                every { courseService.updateCourseById(any(), any()) } returns Mono.just(createCourseResponse())
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

            context("코스가 존재하지 않는 경우") {
                every { courseService.updateCourseById(any(), any()) } throws CourseNotFoundException()
                withMockAdmin()

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .put()
                        .uri("/admin/course/{id}", ID)
                        .bodyValue(createUpdateCourseByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 수정 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateCourseByIdRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteCourseById()는") {
            context("어드민이 코스를 삭제하는 경우") {
                every { courseService.deleteCourseById(any()) } returns Mono.empty()
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

            context("코스가 존재하지 않는 경우") {
                every { courseService.deleteCourseById(any()) } throws CourseNotFoundException()
                withMockAdmin()

                it("상태 코드 404를 반환한다.") {
                    webClient
                        .delete()
                        .uri("/admin/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "코스 삭제 실패(404)",
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