package com.scrumpokerpro.entity.meeting.retro

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class RetroTemplate(
    @Id
    val id: UUID? = null,
    val userId: UUID,
    val columns: Json,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
