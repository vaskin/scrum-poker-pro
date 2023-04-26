package com.scrumpokerpro.dto.issue

data class SearchIssueDto(
    val text: String,
    val jql: Boolean = false,
    val project: String? = null,
    val sprint: String? = null,
    val status: String? = null
)
