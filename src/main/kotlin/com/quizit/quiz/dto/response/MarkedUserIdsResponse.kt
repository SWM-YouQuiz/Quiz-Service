package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Quiz

data class MarkedUserIdsResponse(
    val markedUserIds: HashSet<String>
) {
    companion object {
        operator fun invoke(quiz: Quiz): MarkedUserIdsResponse =
            MarkedUserIdsResponse(markedUserIds = quiz.markedUserIds)
    }
}