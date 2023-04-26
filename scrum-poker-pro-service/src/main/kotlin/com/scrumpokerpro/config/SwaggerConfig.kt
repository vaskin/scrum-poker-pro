package com.scrumpokerpro.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.annotation.AuthenticationPrincipal
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.HttpAuthenticationScheme
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import java.util.Collections.singletonList

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.OAS_30)
            .ignoredParameterTypes(AuthenticationPrincipal::class.java)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(listOf(authenticationScheme()))
            .securityContexts(singletonList(securityContext()))
    }

    fun authenticationScheme(): HttpAuthenticationScheme = HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("BearerToken").build()

    fun defaultAuth(): List<SecurityReference> {
        val authorizationScopes = arrayOf(AuthorizationScope("global", "accessEverything"))
        return listOf(SecurityReference("BearerToken", authorizationScopes))
    }

    fun securityContext(): SecurityContext = SecurityContext.builder()
        .securityReferences(defaultAuth())
        .operationSelector { true }
        .build()
}
