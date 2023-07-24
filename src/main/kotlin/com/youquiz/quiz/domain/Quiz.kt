package com.youquiz.quiz.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Quiz(
    @Id
    var id: String? = null,
    val question: String,
    val answer: Int,
    val solution: String,
    val writer: User,
    val chapterId: String,
    var answerRate: Double,
    var correctCount: Long,
    var incorrectCount: Long,
) {
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()

    fun correctAnswer() {
        correctCount += 1
        changeAnswerRate()
    }

    fun incorrectAnswer() {
        incorrectCount += 1
        changeAnswerRate()
    }

    private fun changeAnswerRate() {
        answerRate = (correctCount.toDouble() / (correctCount + incorrectCount).toDouble()) * 100
    }
}