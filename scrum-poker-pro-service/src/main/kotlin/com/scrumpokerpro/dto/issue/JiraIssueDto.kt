package com.scrumpokerpro.dto.issue

data class JiraIssueDto(
    val id: String,
    val parentId: String? = null,
    val key: String,
    val link: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val type: String,
    val iconUrl: String? = null,
    val subtask: Boolean = false
)
