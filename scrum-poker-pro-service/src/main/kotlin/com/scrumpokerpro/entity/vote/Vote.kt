package com.scrumpokerpro.entity.vote

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Vote(
    @Id
    val id: UUID? = null,
    val storyPoint: String,
    val issueId: UUID,
    val userId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
