package com.quizit.quiz.dto.response

import com.quizit.quiz.domain.Quiz

data class LikedUserIdsResponse(
    val likedUserIds: HashSet<String>,
    val unlikedUserIds: HashSet<String>
) {
    companion object {
        operator fun invoke(quiz: Quiz): LikedUserIdsResponse =
            with(quiz) {
                LikedUserIdsResponse(
                    likedUserIds = likedUserIds,
                    unlikedUserIds = unlikedUserIds
                )
            }
    }
}