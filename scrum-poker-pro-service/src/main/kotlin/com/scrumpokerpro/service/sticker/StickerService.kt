package com.scrumpokerpro.service.sticker

import com.scrumpokerpro.dto.sticker.CreateStickerDto
import com.scrumpokerpro.dto.sticker.StickerDto
import com.scrumpokerpro.dto.sticker.UpdateStickerDto
import org.springframework.http.HttpEntity
import java.util.UUID

interface StickerService {

    suspend fun createSticker(userId: UUID, userName: String, dto: CreateStickerDto): StickerDto

    suspend fun getStickers(userId: UUID, retroColumnId: UUID): List<StickerDto>

    suspend fun updateSticker(id: UUID, userId: UUID, dto: UpdateStickerDto): StickerDto

    suspend fun deleteSticker(id: UUID, userId: UUID)

    suspend fun getAvatar(id: UUID): HttpEntity<ByteArray>
}
