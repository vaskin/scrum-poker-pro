package com.scrumpokerpro.dto.vote

import java.time.LocalDateTime
import java.util.UUID

data class VoteDto(
    val id: UUID,
    val storyPoint: String? = null,
    val issueId: UUID,
    val userId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
