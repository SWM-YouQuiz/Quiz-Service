package com.quizit.quiz.adapter.client

import com.quizit.quiz.dto.response.GetUserByIdResponse
import com.quizit.quiz.exception.UserNotFoundException
import com.quizit.quiz.global.config.getCurrentAuthentication
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
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
            .uri("$url/api/user/user/{id}", userId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${getCurrentAuthentication().token}")
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals) { throw UserNotFoundException() }
            .awaitBody<GetUserByIdResponse>()
}