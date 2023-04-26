package com.scrumpokerpro.config

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    val properties: OAuth2ResourceServerProperties
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        authorizeExchange {
            authorize("/actuator/health", permitAll)
            authorize("/actuator/prometheus", permitAll)
            authorize("/swagger-ui/**", permitAll)
            authorize("/swagger-resources/**", permitAll)
            authorize("/v3/api-docs/**", permitAll)
            authorize(anyExchange, authenticated)
        }

        oauth2ResourceServer {
            bearerTokenConverter = serverAuthenticationConverter()
            jwt {
                jwtAuthenticationConverter = grantedAuthoritiesExtractor()
            }
        }
    }

    @Bean
    fun grantedAuthoritiesExtractor(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val converter = GrantedAuthoritiesConverter()
        return ReactiveJwtAuthenticationConverterAdapter(converter)
    }

    @Bean
    fun serverAuthenticationConverter(): ServerAuthenticationConverter {
        val authenticationConverter = ServerBearerTokenAuthenticationConverter()
        authenticationConverter.setAllowUriQueryParameter(true)
        return authenticationConverter
    }

    @Bean
    fun jwtDecoderByJwkKeySetUri(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(properties.jwt.jwkSetUri)
            .jwsAlgorithm(SignatureAlgorithm.from(properties.jwt.jwsAlgorithm)).build()
    }
}
