package com.youquiz.quiz.adapter.client

import com.youquiz.quiz.dto.GetUserByIdResponse
import com.youquiz.quiz.exception.UserNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    suspend fun getUserById(userId: String) =
        webClient.get()
            .uri("$url/api/user/{id}", userId)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<GetUserByIdResponse>()
}