package com.youquiz.quiz.dto

import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.domain.User
import java.time.LocalDateTime

data class QuizResponse(
    val id: String,
    val question: String,
    val answer: Int,
    val solution: String,
    val writer: User,
    val chapterId: String,
    val answerRate: Long,
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
                    writer = writer,
                    chapterId = chapterId,
                    answerRate = answerRate,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    createdDate = createdDate
                )
            }
    }
}