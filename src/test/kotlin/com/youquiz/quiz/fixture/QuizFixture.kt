package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.domain.User

const val QUESTION = "test"
const val ANSWER = 1
const val SOLUTION = "test"
val WRITER = createUser()
const val CHAPTER_ID = OBJECT_ID
const val ANSWER_RATE = 80L
val CREATED_DATE = LocalDateTime.now()!!

fun createQuiz(
    id: String = OBJECT_ID,
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    writer: User = WRITER,
    chapterId: String = CHAPTER_ID,
    answerRate: Long = ANSWER_RATE,
): Quiz = Quiz(
    id = id,
    question = question,
    answer = answer,
    solution = solution,
    writer = writer,
    chapterId = chapterId,
    answerRate = answerRate,
)