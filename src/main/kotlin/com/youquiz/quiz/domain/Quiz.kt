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
    var answerRate: Long,
    var correctCount: Long,
    var incorrectCount: Long,
) {
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
}