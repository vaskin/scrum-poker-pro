package com.scrumpokerpro.service.sticker

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.sticker.CreateStickerDto
import com.scrumpokerpro.dto.sticker.UpdateStickerDto
import com.scrumpokerpro.entity.sticker.Sticker
import com.scrumpokerpro.repository.StickerRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class StickerServiceImplTest(
    @Autowired
    val stickerService: StickerService,
    @Autowired
    val stickerRepository: StickerRepository
) {

    @Test
    fun `should create sticker`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val userName = "user"
            val dto = CreateStickerDto(
                text = "Sprint completed successfully",
                retroColumnId = UUID.randomUUID()
            )

            val stickerId = stickerService.createSticker(userId, userName, dto).id
            val createdSticker = stickerRepository.findById(stickerId)
            assertEquals(dto.retroColumnId, createdSticker?.retroColumnId)
            assertEquals(dto.text, createdSticker?.text)
            assertEquals(userId, createdSticker?.userId)
            assertEquals(userName, createdSticker?.userName)
            assertEquals(1, createdSticker?.position)
            assertNotNull(createdSticker?.created)
            assertNotNull(createdSticker?.modified)
        }
    }

    @Test
    fun `should update sticker`() {
        runBlocking {
            val sticker = Sticker(
                text = "Sprint completed successfully",
                position = 1,
                retroColumnId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                userName = "user",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                stickerRepository.save(it)
            }
            val dto = UpdateStickerDto(text = "Sprint failed")

            stickerService.updateSticker(sticker.id!!, sticker.userId, dto)
            val updatedSticker = stickerRepository.findById(sticker.id!!)
            assertEquals(dto.text, updatedSticker?.text)
        }
    }

    @Test
    fun `should delete sticker`() {
        runBlocking {
            val sticker = Sticker(
                text = "Sprint completed successfully",
                position = 1,
                retroColumnId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                userName = "user",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                stickerRepository.save(it)
            }

            stickerService.deleteSticker(sticker.id!!, sticker.userId)
            assertNull(stickerRepository.findById(sticker.id!!))
        }
    }
}
