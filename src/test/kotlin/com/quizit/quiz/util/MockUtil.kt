package com.quizit.quiz.util

import com.quizit.quiz.domain.enum.Role
import com.quizit.quiz.fixture.createJwtAuthentication
import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKStubScope
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

infix fun <T> MockKStubScope<Mono<T>, *>.returns(value: T?): MockKAdditionalAnswerScope<Mono<T>, *> =
    this returns Mono.justOrEmpty(value)

infix fun <T> MockKStubScope<Flux<T>, *>.returns(values: List<T>): MockKAdditionalAnswerScope<Flux<T>, *> =
    this returns Flux.fromIterable(values)

fun withMockUser() {
    SecurityContextHolder.getContext().authentication = createJwtAuthentication()
}

fun withMockAdmin() {
    SecurityContextHolder.getContext().authentication =
        createJwtAuthentication(authorities = listOf(SimpleGrantedAuthority(Role.ADMIN.name)))
}