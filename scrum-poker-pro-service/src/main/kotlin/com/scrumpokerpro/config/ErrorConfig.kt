package com.scrumpokerpro.config

import com.scrumpokerpro.exception.WebExceptionHandler
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.result.view.ViewResolver
import java.util.stream.Collectors

@Configuration
class ErrorConfig(val serverProperties: ServerProperties) {

    @Bean
    @Order(HIGHEST_ORDER)
    fun customErrorWebExceptionHandler(
        errorAttributes: ErrorAttributes,
        resources: WebProperties.Resources,
        viewResolvers: ObjectProvider<ViewResolver>,
        serverCodecConfigurer: ServerCodecConfigurer,
        applicationContext: ApplicationContext
    ): ErrorWebExceptionHandler {
        val exceptionHandler = WebExceptionHandler(errorAttributes, resources, this.serverProperties.error, applicationContext)
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()))
        exceptionHandler.setMessageWriters(serverCodecConfigurer.writers)
        exceptionHandler.setMessageReaders(serverCodecConfigurer.readers)
        return exceptionHandler
    }

    companion object {
        const val HIGHEST_ORDER = -2
    }
}
