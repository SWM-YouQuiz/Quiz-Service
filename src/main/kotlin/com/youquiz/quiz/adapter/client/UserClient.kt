package com.youquiz.quiz.adapter.client

import com.youquiz.quiz.dto.FindUserByIdResponse
import com.youquiz.quiz.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class UserClient(
    private val webClient: WebClient
) {
    suspend fun findById(userId: Long) =
        webClient.get()
            .uri("http://localhost:8081/user/{id}", userId)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<FindUserByIdResponse>()
}