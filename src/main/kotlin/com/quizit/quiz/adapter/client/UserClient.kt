package com.quizit.quiz.adapter.client

import com.quizit.quiz.dto.response.UserResponse
import com.quizit.quiz.exception.UserNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class UserClient(
    private val webClient: WebClient,
    @Value("\${url.service.user}")
    private val url: String
) {
    fun getUserById(userId: String): Mono<UserResponse> =
        ReactiveSecurityContextHolder.getContext()
            .flatMap {
                webClient.get()
                    .uri("$url/user/{id}", userId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals) { Mono.error(UserNotFoundException()) }
                    .bodyToMono<UserResponse>()
            }
}