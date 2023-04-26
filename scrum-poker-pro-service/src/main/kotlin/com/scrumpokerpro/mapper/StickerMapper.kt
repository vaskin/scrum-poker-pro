package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.sticker.StickerDto
import com.scrumpokerpro.entity.sticker.Sticker

fun Sticker.toDto(likes: Int = 0, liked: Boolean = false) = StickerDto(
    id = id!!,
    text = text,
    position = position,
    retroColumnId = retroColumnId,
    userId = userId,
    userName = userName,
    likes = likes,
    liked = liked,
    created = created,
    modified = modified
)
