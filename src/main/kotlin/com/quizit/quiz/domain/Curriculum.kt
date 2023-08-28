package com.quizit.quiz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Curriculum(
    @Id
    var id: String? = null,
    val title: String,
    val image: String
)