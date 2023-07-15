package com.youquiz.authentication.global.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.youquiz.authentication.global.dto.ErrorResponse
import com.youquiz.authentication.global.util.logger
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Configuration
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> = mono {
        logger.error { ex.printStackTrace() }

        val errorResponse = if (ex is ServerException) {
            ErrorResponse(code = ex.code, message = ex.message)
        } else {
            ErrorResponse(code = 500, message = "Internal Server Error")
        }

        with(exchange.response) {
            statusCode = HttpStatusCode.valueOf(errorResponse.code)
            headers.contentType = MediaType.APPLICATION_JSON

            writeWith(bufferFactory().wrap(objectMapper.writeValueAsBytes(errorResponse)).toMono()).awaitSingleOrNull()
        }
    }
}