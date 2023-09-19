package com.quizit.quiz.fixture

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

const val ID = "test"
val CREATED_DATE = LocalDateTime.now()!!
val PAGEABLE = PageRequest.of(0, 3)
val objectMapper = ObjectMapper().apply {
    registerModules(JavaTimeModule(), kotlinModule())
}