package com.youquiz.quiz.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringExtension
import org.springframework.context.annotation.Configuration

@Configuration
class ProjectConfiguration : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)
}