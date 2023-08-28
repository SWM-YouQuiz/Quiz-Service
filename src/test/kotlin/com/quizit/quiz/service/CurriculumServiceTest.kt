package com.quizit.quiz.service

import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCreateCurriculumRequest
import com.quizit.quiz.fixture.createCurriculum
import com.quizit.quiz.fixture.createUpdateCurriculumByIdRequest
import com.quizit.quiz.repository.CurriculumRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class CurriculumServiceTest : BehaviorSpec() {
    private val curriculumRepository = mockk<CurriculumRepository>()

    private val curriculumService = CurriculumService(curriculumRepository)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("커리큘럼들이 존재하는 경우") {
            val curriculum = createCurriculum().also {
                coEvery { curriculumRepository.findById(any()) } returns it
                coEvery { curriculumRepository.deleteById(any()) } just runs
            }
            val curriculums = List(3) { curriculum }.apply {
                asFlow().let {
                    coEvery { curriculumRepository.findAll() } returns it
                }
            }
            val updateCurriculumByIdRequest = createUpdateCurriculumByIdRequest(title = "update").also {
                coEvery { curriculumRepository.save(any()) } returns createCurriculum(title = it.title)
            }

            When("유저가 메인 화면에 들어가면") {
                val curriculumResponses = curriculumService.getCurriculums().toList()

                Then("커리큘럼이 주어진다.") {
                    curriculumResponses shouldContainExactly curriculums.map { CurriculumResponse(it) }
                }
            }

            When("어드민이 특정 커리큘럼을 수정하면") {
                val chapterResponse = curriculumService.updateCurriculumById(ID, updateCurriculumByIdRequest)

                Then("해당 커리큘럼이 수정된다.") {
                    chapterResponse.title shouldNotBeEqual curriculum.title
                }
            }

            When("어드민이 특정 커리큘럼을 삭제하면") {
                curriculumService.deleteCurriculumById(ID)

                Then("해당 커리큘럼이 삭제된다.") {
                    coVerify { curriculumRepository.deleteById(any()) }
                }
            }
        }

        Given("어드민이 커리큘럼을 작성 중인 경우") {
            val curriculum = createCurriculum().also {
                coEvery { curriculumRepository.save(any()) } returns it
            }

            When("어드민이 커리큘럼을 제출하면") {
                val curriculumResponse = curriculumService.createCurriculum(createCreateCurriculumRequest())

                Then("커리큘럼이 생성된다.") {
                    curriculumResponse shouldBeEqualToComparingFields CurriculumResponse(curriculum)
                }
            }
        }
    }
}