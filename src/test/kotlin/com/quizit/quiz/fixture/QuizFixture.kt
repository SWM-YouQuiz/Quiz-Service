package com.quizit.quiz.fixture

import com.quizit.quiz.domain.Quiz
import com.quizit.quiz.dto.request.CheckAnswerRequest
import com.quizit.quiz.dto.request.CreateQuizRequest
import com.quizit.quiz.dto.request.UpdateQuizByIdRequest
import com.quizit.quiz.dto.response.CheckAnswerResponse
import com.quizit.quiz.dto.response.LikedUserIdsResponse
import com.quizit.quiz.dto.response.MarkedUserIdsResponse
import com.quizit.quiz.dto.response.QuizResponse
import java.time.LocalDateTime

const val QUESTION = "question"
const val ANSWER = 1
const val SOLUTION = "solution"
val OPTIONS = (0..4).map { "$it" }
const val ANSWER_RATE = 50.0
const val CORRECT_COUNT = 10L
const val INCORRECT_COUNT = 10L
val MARKED_USER_IDS = hashSetOf("1")
val LIKED_USER_IDS = hashSetOf("1")
val UNLIKED_USER_IDS = hashSetOf("1")

fun createCreateQuizRequest(
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    chapterId: String = ID,
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
    chapterId: String = ID,
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
    answer: Int = ANSWER
): CheckAnswerRequest =
    CheckAnswerRequest(answer)

fun createCheckAnswerResponse(
    answer: Int = ANSWER,
    solution: String = SOLUTION
): CheckAnswerResponse =
    CheckAnswerResponse(
        answer = answer,
        solution = solution
    )

fun createQuizResponse(
    id: String = ID,
    question: String = QUESTION,
    writerId: String = ID,
    chapterId: String = ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT,
    markedUserIds: HashSet<String> = MARKED_USER_IDS,
    likedUserIds: HashSet<String> = LIKED_USER_IDS,
    unlikedUserIds: HashSet<String> = UNLIKED_USER_IDS,
    createdDate: LocalDateTime = CREATED_DATE
): QuizResponse =
    QuizResponse(
        id = id,
        question = question,
        writerId = writerId,
        chapterId = chapterId,
        options = options,
        answerRate = answerRate,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        markedUserIds = markedUserIds,
        likedUserIds = likedUserIds,
        unlikedUserIds = unlikedUserIds,
        createdDate = createdDate
    )

fun createMarkedUserIdsResponse(
    markedUserIds: HashSet<String> = MARKED_USER_IDS
): MarkedUserIdsResponse =
    MarkedUserIdsResponse(markedUserIds = markedUserIds)

fun createLikedUserIdsResponse(
    likedUserIds: HashSet<String> = LIKED_USER_IDS,
    unlikedUserIds: HashSet<String> = UNLIKED_USER_IDS
): LikedUserIdsResponse =
    LikedUserIdsResponse(
        likedUserIds = likedUserIds,
        unlikedUserIds = unlikedUserIds
    )

fun createQuiz(
    id: String = ID,
    question: String = QUESTION,
    answer: Int = ANSWER,
    solution: String = SOLUTION,
    writerId: String = ID,
    chapterId: String = ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT,
    markedUserIds: HashSet<String> = MARKED_USER_IDS.toHashSet(),
    likedUserIds: HashSet<String> = LIKED_USER_IDS.toHashSet(),
    unlikedUserIds: HashSet<String> = UNLIKED_USER_IDS.toHashSet(),
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
    markedUserIds = markedUserIds,
    incorrectCount = incorrectCount,
    likedUserIds = likedUserIds,
    unlikedUserIds = unlikedUserIds,
    createdDate = createdDate
)