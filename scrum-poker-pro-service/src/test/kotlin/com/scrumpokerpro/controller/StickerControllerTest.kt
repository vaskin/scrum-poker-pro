package com.scrumpokerpro.controller

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.sticker.CreateStickerDto
import com.scrumpokerpro.dto.sticker.UpdateStickerDto
import com.scrumpokerpro.service.sticker.StickerService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class StickerControllerTest(
    @Autowired
    val webClient: WebTestClient
) {

    @MockBean
    lateinit var stickerService: StickerService

    @ExperimentalCoroutinesApi
    @Test
    fun `should create sticker`() = runBlockingTest {
        val dto = CreateStickerDto(
            text = "Sprint completed successfully",
            retroColumnId = UUID.randomUUID()
        )

        webClient.mutateWith(
            SecurityMockServerConfigurers.mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
                jwt.claim("name", "user")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/stickers")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
        verify(stickerService).createSticker(userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"), userName = "user", dto = dto)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should update sticker`() = runBlockingTest {
        val stickerId = UUID.randomUUID()
        val dto = UpdateStickerDto(
            text = "Sprint failed"
        )

        webClient.mutateWith(
            SecurityMockServerConfigurers.mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/stickers/$stickerId")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
        verify(stickerService).updateSticker(id = stickerId, userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"), dto = dto)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should delete sticker`() = runBlockingTest {
        val stickerId = UUID.randomUUID()

        webClient.mutateWith(
            SecurityMockServerConfigurers.mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri("/stickers/$stickerId")
            .exchange()
            .expectStatus().isOk
        verify(stickerService).deleteSticker(id = stickerId, userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"))
    }
}
