package com.quizit.quiz.global.config

import com.github.jwt.authentication.DefaultJwtAuthentication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun webClient(): WebClient = WebClient.builder()
        .filter { request, next ->
            ReactiveSecurityContextHolder.getContext()
                .map {
                    it.authentication
                        ?.run {
                            ClientRequest.from(request)
                                .header(
                                    HttpHeaders.AUTHORIZATION, "Bearer ${(this as DefaultJwtAuthentication).token}"
                                )
                                .build()
                        } ?: request
                }
                .flatMap { next.exchange(it) }
        }
        .build()
}