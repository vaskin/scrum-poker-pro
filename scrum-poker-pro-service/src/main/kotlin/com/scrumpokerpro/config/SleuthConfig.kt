package com.scrumpokerpro.config

import org.springframework.cloud.sleuth.Tracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.WebFilter

@Configuration
class SleuthConfig {

    @Bean
    fun tracerResponseFilter(tracer: Tracer): WebFilter {
        return WebFilter { exchange, chain ->
            tracer.currentSpan()?.also {
                exchange.response.headers.add("X-B3-TraceId", it.context().traceId())
                exchange.response.headers.add("X-B3-SpanId", it.context().spanId())
            }
            chain.filter(exchange)
        }
    }
}
