package com.scrumpokerpro.service.sticker.like

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.entity.sticker.StickerLike
import com.scrumpokerpro.repository.StickerLikeRepository
import kotlinx.coroutines.runBlocking
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
class LikeServiceImplTest(
    @Autowired
    val likeService: LikeService,
    @Autowired
    val stickerLikeRepository: StickerLikeRepository,
) {

    @Test
    fun `should create like`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val stickerId = UUID.randomUUID()
            val like = likeService.createLike(stickerId = stickerId, userId = userId)

            val createdLike = stickerLikeRepository.findById(like.id!!)
            assertNotNull(createdLike)
        }
    }

    @Test
    fun `should delete like`() {
        runBlocking {
            val stickerId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            StickerLike(
                userId = userId,
                stickerId = stickerId,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).also {
                stickerLikeRepository.save(it)
            }

            likeService.deleteLike(stickerId = stickerId, userId = userId)
            assertNull(stickerLikeRepository.findByStickerIdAndUserId(stickerId, userId))
        }
    }
}
