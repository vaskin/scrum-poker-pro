package com.scrumpokerpro.config

import com.scrumpokerpro.service.jira.ScrumPokerProJiraDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JiraFeignConfig {

    @Bean
    fun scrumPokerProJiraDecoder() = ScrumPokerProJiraDecoder()
}
