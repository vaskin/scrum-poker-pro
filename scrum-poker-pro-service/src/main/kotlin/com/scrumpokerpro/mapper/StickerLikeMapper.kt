package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.sticker.StickerLikeDto
import com.scrumpokerpro.entity.sticker.StickerLike

fun StickerLike.toDto() = StickerLikeDto(
    userId = userId,
    stickerId = stickerId
)
