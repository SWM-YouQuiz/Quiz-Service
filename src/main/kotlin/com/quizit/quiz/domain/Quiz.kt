package com.quizit.quiz.domain

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
    val writerId: String,
    val chapterId: String,
    val options: List<String>,
    var answerRate: Double,
    var correctCount: Long,
    var incorrectCount: Long,
    val markedUserIds: MutableSet<String>,
    val likedUserIds: MutableSet<String>,
    val unlikedUserIds: MutableSet<String>,
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()
) {
    fun correctAnswer() {
        correctCount += 1
        changeAnswerRate()
    }

    fun incorrectAnswer() {
        incorrectCount += 1
        changeAnswerRate()
    }

    fun mark(userId: String) {
        markedUserIds.add(userId)
    }

    fun unmark(userId: String) {
        markedUserIds.remove(userId)
    }

    fun like(userId: String) {
        likedUserIds.add(userId)
    }

    fun unlike(userId: String) {
        unlikedUserIds.add(userId)
    }

    private fun changeAnswerRate() {
        answerRate = (correctCount.toDouble() / (correctCount + incorrectCount).toDouble()) * 100
    }
}