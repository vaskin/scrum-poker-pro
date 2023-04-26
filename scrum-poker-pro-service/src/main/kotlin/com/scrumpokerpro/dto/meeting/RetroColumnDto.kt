package com.scrumpokerpro.dto.meeting

import com.scrumpokerpro.dto.sticker.StickerDto
import java.util.UUID

data class RetroColumnDto(
    val id: UUID? = null,
    val name: String,
    val color: String,
    val stickers: List<StickerDto> = listOf()
)
