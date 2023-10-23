package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.fixture.*
import com.quizit.quiz.repository.CourseRepository
import com.quizit.quiz.repository.QuizRepository
import com.quizit.quiz.util.empty
import com.quizit.quiz.util.getResult
import com.quizit.quiz.util.returns
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CourseServiceTest : BehaviorSpec() {
    private val courseRepository = mockk<CourseRepository>()

    private val quizRepository = mockk<QuizRepository>()

    private val userClient = mockk<UserClient>()

    private val courseService = CourseService(
        courseRepository = courseRepository,
        quizRepository = quizRepository,
        userClient = userClient
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    init {
        Given("코스들이 존재하는 경우") {
            val course = createCourse()
                .also {
                    every { courseRepository.findAllByCurriculumId(any()) } returns listOf(it)
                    every { courseRepository.findById(any<String>()) } returns it
                    every { courseRepository.deleteById(any<String>()) } returns empty()
                    every { quizRepository.findAllByCourseId(any()) } returns listOf(createQuiz())
                    every { userClient.getUserById(any()) } returns createUserResponse()
                }
            val courseResponse = CourseResponse(course)

            When("유저가 메인 화면에 들어가면") {
                val results = listOf(
                    courseService.getCoursesByCurriculumId(ID)
                        .getResult(),
                    courseService.getCourseById(ID)
                        .getResult()
                )
                val result = courseService.getProgressById(ID, ID)
                    .getResult()

                Then("코스가 주어진다.") {
                    results.map {
                        it.expectSubscription()
                            .expectNext(courseResponse)
                            .verifyComplete()
                    }
                }

                Then("코스의 진척도가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createGetProgressByIdResponse())
                        .verifyComplete()
                }
            }

            When("어드민이 특정 코스를 수정하면") {
                val updateCourseByIdRequest = createUpdateCourseByIdRequest(title = "updated_title")
                    .also {
                        every { courseRepository.save(any()) } returns createCourse(title = it.title)
                    }
                val result = courseService.updateCourseById(ID, updateCourseByIdRequest)
                    .getResult()

                Then("해당 코스가 수정된다.") {
                    result.expectSubscription()
                        .assertNext { it shouldNotBeEqual courseResponse }
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
                    every { courseRepository.save(any()) } returns it
                }
            val courseResponse = CourseResponse(course)

            When("어드민이 코스를 제출하면") {
                val result = courseService.createCourse(createCreateCourseRequest())
                    .getResult()

                Then("코스가 생성된다.") {
                    result.expectSubscription()
                        .expectNext(courseResponse)
                        .verifyComplete()
                }
            }
        }
    }
}