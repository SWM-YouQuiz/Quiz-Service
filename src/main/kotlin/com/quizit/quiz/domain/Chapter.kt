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
    var index: Int
) {
    fun update(description: String, document: String, courseId: String, index: Int): Chapter =
        also {
            it.description = description
            it.document = document
            it.courseId = courseId
            it.index = index
        }
}