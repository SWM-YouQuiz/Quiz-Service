package com.quizit.quiz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Chapter(
    @Id
    var id: String? = null,
    val description: String,
    val document: String,
    val courseId: String,
)