package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Quiz
import java.time.LocalDateTime

data class QuizResponse(
    val id: String,
    val question: String,
    val writerId: String,
    val chapterId: String,
    val answerRate: Double,
    val options: List<String>,
    val correctCount: Long,
    val incorrectCount: Long,
    val markedUserIds: Set<String>,
    val likedUserIds: Set<String>,
    val unlikedUserIds: Set<String>,
    val createdDate: LocalDateTime,
) {
    companion object {
        operator fun invoke(quiz: Quiz) =
            with(quiz) {
                QuizResponse(
                    id = id!!,
                    question = question,
                    writerId = writerId,
                    chapterId = chapterId,
                    options = options,
                    answerRate = answerRate,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    markedUserIds = markedUserIds,
                    likedUserIds = likedUserIds,
                    unlikedUserIds = unlikedUserIds,
                    createdDate = createdDate
                )
            }
    }
}