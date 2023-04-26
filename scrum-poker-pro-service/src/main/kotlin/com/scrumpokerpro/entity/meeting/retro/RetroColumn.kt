package com.scrumpokerpro.entity.meeting.retro

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class RetroColumn(
    @Id
    val id: UUID? = null,
    val name: String,
    val color: String,
    val position: Int,
    val meetingId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
