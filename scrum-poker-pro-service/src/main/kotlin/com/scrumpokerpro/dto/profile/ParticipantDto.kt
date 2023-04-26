package com.scrumpokerpro.dto.profile

import java.time.LocalDateTime
import java.util.UUID

data class ParticipantDto(
    val userId: UUID,
    val name: String,
    val avatar: String? = null,
    val contentType: String? = null,
    val fileName: String? = null,
    val lastActivity: LocalDateTime,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
