package com.scrumpokerpro.service.sticker.like

import com.scrumpokerpro.entity.sticker.StickerLike
import java.util.UUID

interface LikeService {

    suspend fun getLikes(stickerId: UUID): List<StickerLike>

    suspend fun createLike(stickerId: UUID, userId: UUID): StickerLike

    suspend fun deleteLike(stickerId: UUID, userId: UUID)
}
