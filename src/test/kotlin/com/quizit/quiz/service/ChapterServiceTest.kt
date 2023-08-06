package com.quizit.quiz.service

import com.quizit.quiz.dto.response.ChapterResponse
import com.quizit.quiz.fixture.*
import com.quizit.quiz.repository.ChapterRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class ChapterServiceTest : BehaviorSpec() {
    private val chapterRepository = mockk<ChapterRepository>()

    private val chapterService = ChapterService(chapterRepository)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("코스와 각각의 코스에 속하는 챕터들이 존재하는 경우") {
            val chapter = createChapter().also {
                coEvery { chapterRepository.findById(any()) } returns it
                coEvery { chapterRepository.deleteById(any()) } just runs
            }
            val chapters = List(3) { chapter }.apply {
                asFlow().let {
                    coEvery { chapterRepository.findAllByCourseId(any()) } returns it
                }
            }
            val updateChapterByIdRequest = createUpdateChapterByIdRequest(description = "update").also {
                coEvery { chapterRepository.save(any()) } returns createChapter(description = it.description)
            }

            When("유저가 코스에 들어가면") {
                val chapterResponses = chapterService.getChaptersByCourseId(COURSE_ID).toList()

                Then("해당 코스에 속하는 챕터들이 주어진다.") {
                    chapterResponses shouldContainExactly chapters.map { ChapterResponse(it) }
                }
            }

            When("어드민이 특정 챕터를 수정하면") {
                val chapterResponse = chapterService.updateChapterById(ID, updateChapterByIdRequest)

                Then("해당 챕터가 수정된다.") {
                    chapterResponse.description shouldNotBeEqual chapter.description
                }
            }

            When("어드민이 특정 챕터를 삭제하면") {
                chapterService.deleteChapterById(ID)

                Then("해당 챕터가 삭제된다.") {
                    coVerify { chapterRepository.deleteById(any()) }
                }
            }
        }

        Given("어드민이 챕터를 작성 중인 경우") {
            val chapter = createChapter().also {
                coEvery { chapterRepository.save(any()) } returns it
            }

            When("어드민이 챕터를 제출하면") {
                val chapterResponse = chapterService.createChapter(createCreateChapterRequest())

                Then("챕터가 생성된다.") {
                    chapterResponse shouldBeEqualToComparingFields ChapterResponse(chapter)
                }
            }
        }

    }
}