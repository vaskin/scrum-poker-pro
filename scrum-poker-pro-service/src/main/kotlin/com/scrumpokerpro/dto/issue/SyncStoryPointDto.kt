package com.scrumpokerpro.dto.issue

data class SyncStoryPointDto(
    val fieldId: String,
    val schema: Map<String, Any> = mapOf()
)
