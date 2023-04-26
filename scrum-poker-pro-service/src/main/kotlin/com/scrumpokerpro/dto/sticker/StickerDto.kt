package com.scrumpokerpro.dto.sticker

import java.time.LocalDateTime
import java.util.UUID

data class StickerDto(
    val id: UUID,
    val text: String,
    val position: Int,
    val retroColumnId: UUID,
    val userId: UUID,
    val userName: String,
    val likes: Int,
    val liked: Boolean = false,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
