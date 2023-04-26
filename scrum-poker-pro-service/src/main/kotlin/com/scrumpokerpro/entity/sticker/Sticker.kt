package com.scrumpokerpro.entity.sticker

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Sticker(
    @Id
    val id: UUID? = null,
    val text: String,
    val position: Int,
    val retroColumnId: UUID,
    val userId: UUID,
    val userName: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
