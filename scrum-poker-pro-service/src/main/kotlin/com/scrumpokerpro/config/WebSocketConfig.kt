package com.scrumpokerpro.config

import com.scrumpokerpro.websocket.MeetingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig(
    val meetingHandler: MeetingHandler
) {

    @Bean
    fun webSocketMapping(): HandlerMapping {
        return SimpleUrlHandlerMapping().apply {
            order = 1
            urlMap = mapOf<String, WebSocketHandler>(
                "/ws/meetings/**" to meetingHandler
            )
        }
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }
}
