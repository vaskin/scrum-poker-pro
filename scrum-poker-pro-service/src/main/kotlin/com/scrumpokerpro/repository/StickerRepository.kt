package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.sticker.Sticker
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface StickerRepository : CoroutineCrudRepository<Sticker, UUID> {

    suspend fun findByRetroColumnId(retroColumnId: UUID): List<Sticker>
}
