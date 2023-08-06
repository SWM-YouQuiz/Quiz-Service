package com.quizit.quiz.config

import com.quizit.quiz.fixture.jwtProvider
import com.quizit.quiz.global.config.SecurityConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class SecurityTestConfiguration {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        SecurityConfiguration().filterChain(http, jwtProvider)
}