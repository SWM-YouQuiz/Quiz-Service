package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.domain.User

const val QUESTION = "test"
const val ANSWER = 1
const val SOLUTION = "test"
val WRITER = createUser()
const val CHAPTER_ID = OBJECT_ID
val OPTIONS = (1..5).map { "test_$it" }
const val ANSWER_RATE = 50.0
const val CORRECT_COUNT = 10L
const val INCORRECT_COUNT = 10L

fun createQuiz(
    id: String = OBJECT_ID,
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    writer: User = WRITER,
    chapterId: String = CHAPTER_ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT
): Quiz = Quiz(
    id = id,
    question = question,
    answer = answer,
    solution = solution,
    writer = writer,
    chapterId = chapterId,
    options = options,
    answerRate = answerRate,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
)