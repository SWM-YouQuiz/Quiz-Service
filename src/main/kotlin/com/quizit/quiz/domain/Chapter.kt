package com.quizit.quiz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Chapter(
    @Id
    var id: String? = null,
    var description: String,
    var document: String,
    var courseId: String,
) {
    fun update(description: String, document: String, courseId: String): Chapter =
        also {
            it.description = description
            it.document = document
            it.courseId = courseId
        }
}