package com.quizit.quiz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Course(
    @Id
    var id: String? = null,
    var title: String,
    var image: String,
    var curriculumId: String
) {
    fun update(title: String, image: String, curriculumId: String): Course =
        also {
            this.title = title
            this.image = image
            this.curriculumId = curriculumId
        }
}