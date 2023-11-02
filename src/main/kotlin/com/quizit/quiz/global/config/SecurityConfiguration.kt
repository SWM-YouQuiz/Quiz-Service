package com.quizit.quiz.global.config

import com.github.jwt.authentication.JwtAuthenticationFilter
import com.github.jwt.core.JwtProvider
import com.quizit.quiz.domain.enum.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@EnableWebFluxSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun filterChain(
        http: ServerHttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityWebFilterChain =
        with(http) {
            csrf { it.disable() }
            formLogin { it.disable() }
            httpBasic { it.disable() }
            logout { it.disable() }
            requestCache { it.disable() }
            securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            exceptionHandling { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            authorizeExchange {
                it.pathMatchers("/quiz/admin/**")
                    .hasAuthority(Role.ADMIN.name)
                    .pathMatchers("/actuator/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            build()
        }

    @Bean
    fun jwtAuthenticationFilter(jwtProvider: JwtProvider): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtProvider)
}