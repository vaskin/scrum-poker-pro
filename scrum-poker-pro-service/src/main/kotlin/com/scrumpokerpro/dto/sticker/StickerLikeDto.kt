package com.scrumpokerpro.dto.sticker

import java.util.UUID

data class StickerLikeDto(
    val stickerId: UUID,
    val userId: UUID,
)
