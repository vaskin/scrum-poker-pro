package com.scrumpokerpro.dto.sticker

import java.util.UUID

data class CreateStickerDto(
    val text: String,
    val retroColumnId: UUID
)
