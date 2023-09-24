package com.quizit.quiz.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Quiz(
    @Id
    var id: String? = null,
    var question: String,
    var answer: Int,
    var solution: String,
    var writerId: String,
    var chapterId: String,
    var options: List<String>,
    var answerRate: Double,
    var correctCount: Long,
    var incorrectCount: Long,
    val markedUserIds: HashSet<String>,
    val likedUserIds: HashSet<String>,
    val unlikedUserIds: HashSet<String>,
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

    fun update(question: String, answer: Int, solution: String, chapterId: String, options: List<String>): Quiz =
        also {
            it.question = question
            it.answer = answer
            it.solution = solution
            it.chapterId = chapterId
            it.options = options
        }
}