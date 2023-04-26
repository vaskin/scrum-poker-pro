package com.scrumpokerpro.service.sticker.like

import com.scrumpokerpro.entity.sticker.StickerLike
import com.scrumpokerpro.repository.StickerLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class LikeServiceImpl(
    val stickerLikeRepository: StickerLikeRepository,
) : LikeService {

    @Transactional
    override suspend fun createLike(stickerId: UUID, userId: UUID): StickerLike {
        val now = LocalDateTime.now()
        return stickerLikeRepository.findByStickerIdAndUserId(stickerId, userId) ?: StickerLike(
            stickerId = stickerId,
            userId = userId,
            created = now,
            modified = now
        ).let {
            stickerLikeRepository.save(it)
        }
    }

    @Transactional
    override suspend fun deleteLike(stickerId: UUID, userId: UUID) {
        stickerLikeRepository.findByStickerIdAndUserId(stickerId, userId)?.also {
            stickerLikeRepository.delete(it)
        }
    }

    override suspend fun getLikes(stickerId: UUID): List<StickerLike> {
        return stickerLikeRepository.findByStickerId(stickerId)
    }
}
