package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Curriculum

data class CurriculumResponse(
    val id: String,
    val title: String,
    val image: String,
) {
    companion object {
        operator fun invoke(curriculum: Curriculum): CurriculumResponse =
            with(curriculum) {
                CurriculumResponse(
                    id = id!!,
                    title = title,
                    image = image
                )
            }
    }
}