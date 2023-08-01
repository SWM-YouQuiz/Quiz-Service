package com.youquiz.quiz.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfiguration(
    @Value("\${url.frontend}")
    private val frontendUrl: String
) : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").apply {
            allowedOriginPatterns(frontendUrl)
            allowCredentials(true)
        }
    }
}