package com.youquiz.quiz.dto

import com.youquiz.quiz.domain.Quiz
import java.time.LocalDateTime

data class QuizResponse(
    val id: String,
    val question: String,
    val answer: Int,
    val solution: String,
    val writerId: String,
    val chapterId: String,
    val answerRate: Double,
    val options: List<String>,
    val correctCount: Long,
    val incorrectCount: Long,
    val createdDate: LocalDateTime,
) {
    companion object {
        operator fun invoke(quiz: Quiz) =
            with(quiz) {
                QuizResponse(
                    id = id!!,
                    question = question,
                    answer = answer,
                    solution = solution,
                    writerId = writerId,
                    chapterId = chapterId,
                    options = options,
                    answerRate = answerRate,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    createdDate = createdDate
                )
            }
    }
}