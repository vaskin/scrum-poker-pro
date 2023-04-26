package com.scrumpokerpro.dto.issue

data class UpdateJiraIssueDto(
    val issueId: String,
    val storyPoints: String,
    val fieldId: String,
    val schema: Map<String, Any> = mapOf()
)
