package com.quizit.quiz.adapter.client

import com.quizit.quiz.dto.response.UserResponse
import com.quizit.quiz.exception.UserNotFoundException
import com.quizit.quiz.global.annotation.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    fun getUserById(userId: String): Mono<UserResponse> =
        webClient.get()
            .uri("$url/user/{id}", userId)
            .exchangeToMono {
                when (it.statusCode()) {
                    HttpStatus.NOT_FOUND -> Mono.error(UserNotFoundException())
                    HttpStatus.OK -> it.bodyToMono<UserResponse>()
                    else -> it.createError()
                }
            }
}