package com.youquiz.quiz.config

import com.youquiz.quiz.fixture.jwtProvider
import com.youquiz.quiz.global.config.SecurityConfiguration
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