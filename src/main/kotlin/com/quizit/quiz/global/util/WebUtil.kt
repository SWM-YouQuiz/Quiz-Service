package com.quizit.quiz.global.util

import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun RouterFunctionDsl.queryParams(vararg name: String): RequestPredicate =
    name.map { queryParam(it) { true } }.reduce { total, next -> total and next }

inline fun <reified T> ServerRequest.queryParamNotNull(name: String): T =
    this.queryParamOrNull(name)!!.run {
        when (T::class) {
            Int::class -> toInt()
            Boolean::class -> toBoolean()
            else -> this
        } as T
    }