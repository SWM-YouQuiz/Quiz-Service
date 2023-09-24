package com.quizit.quiz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Curriculum(
    @Id
    var id: String? = null,
    var title: String,
    var image: String
) {
    fun update(title: String, image: String): Curriculum =
        also {
            it.title = title
            it.image = image
        }
}