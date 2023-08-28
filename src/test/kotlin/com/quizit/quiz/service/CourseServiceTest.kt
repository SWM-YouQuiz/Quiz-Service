package com.quizit.quiz.service

import com.quizit.quiz.dto.response.CourseResponse
import com.quizit.quiz.fixture.*
import com.quizit.quiz.repository.CourseRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class CourseServiceTest : BehaviorSpec() {
    private val courseRepository = mockk<CourseRepository>()

    private val courseService = CourseService(courseRepository)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("코스들이 존재하는 경우") {
            val course = createCourse().also {
                coEvery { courseRepository.findById(any()) } returns it
                coEvery { courseRepository.deleteById(any()) } just runs
            }
            val courses = listOf(course).apply {
                asFlow().let {
                    coEvery { courseRepository.findAllByCurriculumId(any()) } returns it
                }
            }
            val updateCourseByIdRequest = createUpdateCourseByIdRequest(title = "update").also {
                coEvery { courseRepository.save(any()) } returns createCourse(title = it.title)
            }

            When("유저가 커리큘럼에 들어가면") {
                val courseResponses = courseService.getCoursesByCurriculumId(CURRICULUM_ID).toList()

                Then("해당 커리큘럼에 속하는 코스들이 주어진다.") {
                    courseResponses shouldContainExactly courses.map { CourseResponse(it) }
                }
            }

            When("어드민이 특정 코스를 수정하면") {
                val courseResponse = courseService.updateCourseById(ID, updateCourseByIdRequest)

                Then("해당 코스가 수정된다.") {
                    courseResponse.title shouldNotBeEqual course.title
                }
            }

            When("어드민이 특정 코스를 삭제하면") {
                courseService.deleteCourseById(ID)

                Then("해당 코스가 삭제된다.") {
                    coVerify { courseRepository.deleteById(any()) }
                }
            }
        }

        Given("어드민이 코스를 작성 중인 경우") {
            val course = createCourse().also {
                coEvery { courseRepository.save(any()) } returns it
            }

            When("어드민이 챕터를 제출하면") {
                val courseResponse = courseService.createCourse(createCreateCourseRequest())

                Then("챕터가 생성된다.") {
                    courseResponse shouldBeEqualToComparingFields CourseResponse(course)
                }
            }
        }
    }
}