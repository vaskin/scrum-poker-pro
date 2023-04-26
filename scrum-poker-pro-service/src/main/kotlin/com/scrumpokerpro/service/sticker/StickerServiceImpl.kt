package com.scrumpokerpro.service.sticker

import com.scrumpokerpro.dto.sticker.CreateStickerDto
import com.scrumpokerpro.dto.sticker.StickerDto
import com.scrumpokerpro.dto.sticker.UpdateStickerDto
import com.scrumpokerpro.entity.sticker.Sticker
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.repository.StickerRepository
import com.scrumpokerpro.service.profile.ProfileService
import com.scrumpokerpro.service.sticker.like.LikeService
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.newdv.bg.report.util.toHttpEntity
import java.time.LocalDateTime
import java.util.UUID

@Service
class StickerServiceImpl(
    val stickerRepository: StickerRepository,
    val profileService: ProfileService,
    val likeService: LikeService,
) : StickerService {

    @Transactional
    override suspend fun createSticker(userId: UUID, userName: String, dto: CreateStickerDto): StickerDto {
        val now = LocalDateTime.now()
        val columnStickers = stickerRepository.findByRetroColumnId(dto.retroColumnId)
        return Sticker(
            text = dto.text,
            position = columnStickers.maxByOrNull { it.position }?.position?.let {
                it + 1
            } ?: 1,
            retroColumnId = dto.retroColumnId,
            userId = userId,
            userName = profileService.getName(name = userName, userId = userId),
            created = now,
            modified = now
        ).let {
            stickerRepository.save(it)
        }.toDto()
    }

    override suspend fun getStickers(userId: UUID, retroColumnId: UUID): List<StickerDto> {
        return stickerRepository.findByRetroColumnId(retroColumnId).map {
            val likes = likeService.getLikes(it.id!!)
            it.toDto(likes = likes.size, liked = likes.map { like -> like.userId }.contains(userId))
        }.sortedByDescending { it.position }
    }

    @Transactional
    override suspend fun updateSticker(id: UUID, userId: UUID, dto: UpdateStickerDto): StickerDto {
        return stickerRepository.findById(id)?.also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
            stickerRepository.save(
                it.copy(text = dto.text, modified = LocalDateTime.now())
            )
        }?.toDto() ?: throw EntityNotFoundException()
    }

    @Transactional
    override suspend fun deleteSticker(id: UUID, userId: UUID) {
        stickerRepository.findById(id)?.also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
            stickerRepository.deleteById(id)
        }
    }

    override suspend fun getAvatar(id: UUID): HttpEntity<ByteArray> {
        val sticker = stickerRepository.findById(id) ?: throw EntityNotFoundException()
        return profileService.getProfile(sticker.userId)?.avatarKey?.let {
            val avatar = profileService.getAvatar(userId = sticker.userId)
            avatar.asByteArray().toHttpEntity(
                filename = avatar.response().contentDisposition(),
                type = MediaType.parseMediaType(avatar.response().contentType())
            )
        } ?: byteArrayOf().toHttpEntity(
            filename = "not_found",
            type = MediaType.IMAGE_JPEG
        )
    }
}
