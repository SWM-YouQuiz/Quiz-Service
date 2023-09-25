package com.quizit.quiz.service

import com.quizit.quiz.dto.response.CurriculumResponse
import com.quizit.quiz.fixture.ID
import com.quizit.quiz.fixture.createCreateCurriculumRequest
import com.quizit.quiz.fixture.createCurriculum
import com.quizit.quiz.fixture.createUpdateCurriculumByIdRequest
import com.quizit.quiz.repository.CurriculumRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CurriculumServiceTest : BehaviorSpec() {
    private val curriculumRepository = mockk<CurriculumRepository>()

    private val curriculumService = CurriculumService(curriculumRepository)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("커리큘럼들이 존재하는 경우") {
            val curriculum = createCurriculum()
                .also {
                    every { curriculumRepository.findAll() } returns Flux.just(it)
                    every { curriculumRepository.findById(any<String>()) } returns Mono.just(it)
                    every { curriculumRepository.deleteById(any<String>()) } returns Mono.empty()
                }
            val curriculumResponse = CurriculumResponse(curriculum)

            When("유저가 메인 화면에 들어가면") {
                val results = listOf(
                    StepVerifier.create(curriculumService.getCurriculums()),
                    StepVerifier.create(curriculumService.getCurriculumById(ID))
                )

                Then("커리큘럼이 주어진다.") {
                    results.map {
                        it.expectSubscription()
                            .expectNext(curriculumResponse)
                            .verifyComplete()
                    }
                }
            }

            When("어드민이 특정 커리큘럼을 수정하면") {
                val updateCurriculumByIdRequest = createUpdateCurriculumByIdRequest(title = "updated_title")
                    .also {
                        every { curriculumRepository.save(any()) } returns Mono.just(createCurriculum(title = it.title))
                    }
                val result =
                    StepVerifier.create(curriculumService.updateCurriculumById(ID, updateCurriculumByIdRequest))

                Then("해당 커리큘럼이 수정된다.") {
                    result.expectSubscription()
                        .assertNext { it shouldNotBeEqual curriculumResponse }
                        .verifyComplete()
                }
            }

            When("어드민이 특정 커리큘럼을 삭제하면") {
                curriculumService.deleteCurriculumById(ID)
                    .subscribe()

                Then("해당 커리큘럼이 삭제된다.") {
                    verify { curriculumRepository.deleteById(any<String>()) }
                }
            }
        }

        Given("어드민이 커리큘럼을 작성 중인 경우") {
            val curriculum = createCurriculum()
                .also {
                    every { curriculumRepository.save(any()) } returns Mono.just(it)
                }
            val curriculumResponse = CurriculumResponse(curriculum)

            When("어드민이 커리큘럼을 제출하면") {
                val result = StepVerifier.create(curriculumService.createCurriculum(createCreateCurriculumRequest()))

                Then("커리큘럼이 생성된다.") {
                    result.expectSubscription()
                        .expectNext(curriculumResponse)
                        .verifyComplete()
                }
            }
        }
    }
}