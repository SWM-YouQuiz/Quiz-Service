package com.youquiz.quiz.fixture

import com.youquiz.quiz.domain.Quiz
import com.youquiz.quiz.dto.request.CheckAnswerRequest
import com.youquiz.quiz.dto.request.CreateQuizRequest
import com.youquiz.quiz.dto.request.UpdateQuizByIdRequest
import com.youquiz.quiz.dto.response.CheckAnswerResponse
import com.youquiz.quiz.dto.response.QuizResponse
import java.time.LocalDateTime

const val QUESTION = "question"
const val ANSWER = 1
const val SOLUTION = "solution"
const val WRITER_ID = ID
const val CHAPTER_ID = ID
val OPTIONS = (0..4).map { "option_$it" }
const val ANSWER_RATE = 50.0
const val CORRECT_COUNT = 10L
const val INCORRECT_COUNT = 10L
val LIKED_USER_IDS = mutableSetOf("user_1")
const val IS_ANSWER = true

fun createCreateQuizRequest(
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    chapterId: String = CHAPTER_ID,
    options: List<String> = OPTIONS,
): CreateQuizRequest =
    CreateQuizRequest(
        question = question,
        answer = answer,
        solution = solution,
        chapterId = chapterId,
        options = options
    )

fun createUpdateQuizByIdRequest(
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    chapterId: String = CHAPTER_ID,
    options: List<String> = OPTIONS,
): UpdateQuizByIdRequest =
    UpdateQuizByIdRequest(
        question = question,
        answer = answer,
        solution = solution,
        chapterId = chapterId,
        options = options
    )

fun createCheckAnswerRequest(
    quizId: String = ID,
    answer: Int = ANSWER
): CheckAnswerRequest =
    CheckAnswerRequest(
        quizId = quizId,
        answer = answer
    )

fun createCheckAnswerResponse(
    isAnswer: Boolean = IS_ANSWER
): CheckAnswerResponse =
    CheckAnswerResponse(isAnswer)

fun createQuizResponse(
    id: String = ID,
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    writerId: String = WRITER_ID,
    chapterId: String = CHAPTER_ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT,
    likedUserIds: Set<String> = LIKED_USER_IDS,
    createdDate: LocalDateTime = CREATED_DATE
): QuizResponse =
    QuizResponse(
        id = id,
        question = question,
        answer = answer,
        solution = solution,
        writerId = writerId,
        chapterId = chapterId,
        options = options,
        answerRate = answerRate,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        likedUserIds = likedUserIds,
        createdDate = createdDate
    )

fun createQuiz(
    id: String = ID,
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    writerId: String = WRITER_ID,
    chapterId: String = CHAPTER_ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT,
    likedUserIds: MutableSet<String> = LIKED_USER_IDS.toMutableSet(),
    createdDate: LocalDateTime = CREATED_DATE
): Quiz = Quiz(
    id = id,
    question = question,
    answer = answer,
    solution = solution,
    writerId = writerId,
    chapterId = chapterId,
    options = options,
    answerRate = answerRate,
    correctCount = correctCount,
    likedUserIds = likedUserIds,
    incorrectCount = incorrectCount,
    createdDate = createdDate
)