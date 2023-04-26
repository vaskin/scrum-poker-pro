package com.scrumpokerpro.service.jira

import com.scrumpokerpro.config.JiraFeignConfig
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.UpdateJiraIssueDto
import feign.Headers
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "scrumPokerProJiraClient", url = "\${scrum-poker-pro.jira.url}", configuration = [JiraFeignConfig::class])
@Headers("Accept: application/json")
interface ScrumPokerProJiraClient {

    @PutMapping("/issues")
    @Headers("Content-Type: application/json")
    fun updateIssue(updateJiraIssue: UpdateJiraIssueDto, @RequestHeader("Authorization") token: String): Mono<Void>

    @GetMapping("/issues")
    @Headers("Content-Type: application/json")
    fun getIssues(@SpringQueryMap searchIssue: Map<String, Any>, @RequestHeader("Authorization") token: String): Flux<JiraIssueDto>
}
