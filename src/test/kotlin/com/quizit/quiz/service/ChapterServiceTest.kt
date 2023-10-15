package com.quizit.quiz.service

import com.quizit.quiz.adapter.client.UserClient
import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.fixture.*
import com.quizit.quiz.repository.ChapterRepository
import com.quizit.quiz.repository.QuizRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class ChapterServiceTest : BehaviorSpec() {
    private val chapterRepository = mockk<ChapterRepository>()

    private val quizRepository = mockk<QuizRepository>()

    private val userClient = mockk<UserClient>()

    private val chapterService = ChapterService(
        chapterRepository = chapterRepository,
        quizRepository = quizRepository,
        userClient = userClient
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("챕터들이 존재하는 경우") {
            val chapter = createChapter()
                .also {
                    every { chapterRepository.findAllByCourseIdOrderByIndex(any()) } returns Flux.just(it)
                    every { chapterRepository.findById(any<String>()) } returns Mono.just(it)
                    every { chapterRepository.deleteById(any<String>()) } returns Mono.empty()
                    every { quizRepository.findAllByChapterId(any()) } returns Flux.just(createQuiz())
                    every { userClient.getUserById(any()) } returns Mono.just(createUserResponse())
                }
            val chapterResponse = ChapterResponse(chapter)

            When("유저가 메인 화면에 들어가면") {
                val results = listOf(
                    StepVerifier.create(chapterService.getChaptersByCourseId(ID)),
                    StepVerifier.create(chapterService.getChapterById(ID))
                )
                val result = StepVerifier.create(chapterService.getProgressById(ID, ID))

                Then("챕터가 주어진다.") {
                    results.map {
                        it.expectSubscription()
                            .expectNext(chapterResponse)
                            .verifyComplete()
                    }
                }

                Then("챕터의 진척도가 주어진다.") {
                    result.expectSubscription()
                        .expectNext(createGetProgressByIdResponse())
                        .verifyComplete()
                }
            }

            When("어드민이 특정 챕터를 수정하면") {
                val updateChapterByIdRequest = createUpdateChapterByIdRequest(description = "updated_description")
                    .also {
                        every { chapterRepository.save(any()) } returns Mono.just(
                            createChapter(description = it.description)
                        )
                    }
                val result =
                    StepVerifier.create(chapterService.updateChapterById(ID, updateChapterByIdRequest))

                Then("해당 챕터가 수정된다.") {
                    result.expectSubscription()
                        .assertNext { it shouldNotBeEqual chapterResponse }
                        .verifyComplete()
                }
            }

            When("어드민이 특정 챕터를 삭제하면") {
                chapterService.deleteChapterById(ID)
                    .subscribe()

                Then("해당 챕터가 삭제된다.") {
                    verify { chapterRepository.deleteById(any<String>()) }
                }
            }
        }

        Given("어드민이 챕터를 작성 중인 경우") {
            val chapter = createChapter()
                .also {
                    every { chapterRepository.save(any()) } returns Mono.just(it)
                }
            val chapterResponse = ChapterResponse(chapter)

            When("어드민이 챕터를 제출하면") {
                val result = StepVerifier.create(chapterService.createChapter(createCreateChapterRequest()))

                Then("챕터가 생성된다.") {
                    result.expectSubscription()
                        .expectNext(chapterResponse)
                        .verifyComplete()
                }
            }
        }
    }
}