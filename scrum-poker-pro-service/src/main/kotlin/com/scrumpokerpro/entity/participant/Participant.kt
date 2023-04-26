package com.scrumpokerpro.entity.participant

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Participant(
    @Id
    val id: UUID? = null,
    val name: String,
    val meetingId: UUID,
    val userId: UUID,
    val lastActivity: LocalDateTime,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
