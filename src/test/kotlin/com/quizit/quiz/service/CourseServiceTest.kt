package com.quizit.quiz.service

import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCourse
import com.quizit.quiz.fixture.createCreateCourseRequest
import com.quizit.quiz.fixture.createUpdateCourseByIdRequest
import com.quizit.quiz.repository.CourseRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CourseServiceTest : BehaviorSpec() {
    private val courseRepository = mockk<CourseRepository>()

    private val courseService = CourseService(courseRepository)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("코스들이 존재하는 경우") {
            val course = createCourse()
                .also {
                    every { courseRepository.findAllByCurriculumId(any()) } returns Flux.just(it)
                    every { courseRepository.findById(any<String>()) } returns Mono.just(it)
                    every { courseRepository.deleteById(any<String>()) } returns Mono.empty()
                }
            val courseResponse = CourseResponse(course)

            When("유저가 메인 화면에 들어가면") {
                val results = listOf(
                    StepVerifier.create(courseService.getCoursesByCurriculumId(ID)),
                    StepVerifier.create(courseService.getCourseById(ID))
                )

                Then("코스가 주어진다.") {
                    results.map {
                        it.expectSubscription()
                            .expectNext(courseResponse)
                            .verifyComplete()
                    }
                }
            }

            When("어드민이 특정 코스를 수정하면") {
                val updateCourseByIdRequest = createUpdateCourseByIdRequest(title = "updated_title")
                    .also {
                        every { courseRepository.save(any()) } returns Mono.just(createCourse(title = it.title))
                    }
                val result =
                    StepVerifier.create(courseService.updateCourseById(ID, updateCourseByIdRequest))

                Then("해당 코스가 수정된다.") {
                    result.expectSubscription()
                        .assertNext { it shouldNotBeEqualToComparingFields courseResponse }
                        .verifyComplete()
                }
            }

            When("어드민이 특정 코스를 삭제하면") {
                courseService.deleteCourseById(ID)
                    .subscribe()

                Then("해당 코스가 삭제된다.") {
                    verify { courseRepository.deleteById(any<String>()) }
                }
            }
        }

        Given("어드민이 코스를 작성 중인 경우") {
            val course = createCourse()
                .also {
                    every { courseRepository.save(any()) } returns Mono.just(it)
                }
            val courseResponse = CourseResponse(course)

            When("어드민이 코스를 제출하면") {
                val result = StepVerifier.create(courseService.createCourse(createCreateCourseRequest()))

                Then("코스가 생성된다.") {
                    result.expectSubscription()
                        .expectNext(courseResponse)
                        .verifyComplete()
                }
            }
        }
    }
}