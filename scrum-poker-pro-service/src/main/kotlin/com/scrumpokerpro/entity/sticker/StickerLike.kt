package com.scrumpokerpro.entity.sticker

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class StickerLike(
    @Id
    val id: UUID? = null,
    val stickerId: UUID,
    val userId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
