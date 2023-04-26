package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.sticker.StickerLike
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface StickerLikeRepository : CoroutineCrudRepository<StickerLike, UUID> {

    suspend fun findByStickerIdAndUserId(stickerId: UUID, userId: UUID): StickerLike?

    suspend fun findByStickerId(stickerId: UUID): List<StickerLike>
}
