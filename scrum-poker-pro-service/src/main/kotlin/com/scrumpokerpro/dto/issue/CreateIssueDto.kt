package com.scrumpokerpro.dto.issue

data class CreateIssueDto(
    val title: String,
    val description: String? = null,
    val link: String? = null,
    val type: String
)
