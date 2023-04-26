package com.scrumpokerpro.controller

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.meeting.RetroColumnDto
import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import com.scrumpokerpro.service.retrotemplate.RetroTemplateService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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
class TemplateControllerTest(
    @Autowired
    val webClient: WebTestClient
) {

    @MockBean
    lateinit var retroTemplateService: RetroTemplateService

    @ExperimentalCoroutinesApi
    @Test
    fun `should get template`() = runBlockingTest {
        val dto = RetroTemplateDto(
            columns = listOf(
                RetroColumnDto(
                    name = "Ideas",
                    color = "#FF0000"
                )
            )
        )
        Mockito.`when`(
            retroTemplateService.getTemplate(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803")
            )
        ).thenReturn(dto)

        val response = webClient.mutateWith(
            SecurityMockServerConfigurers.mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .get()
            .uri("/templates")
            .exchange()
            .expectStatus().isOk
            .expectBody(RetroTemplateDto::class.java)
            .returnResult().responseBody
        Assertions.assertTrue(response?.columns?.size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should create retro template`() = runBlockingTest {
        val dto = RetroTemplateDto(
            columns = listOf(
                RetroColumnDto(
                    name = "Ideas",
                    color = "#FF0000"
                )
            )
        )
        Mockito.`when`(
            retroTemplateService.createTemplate(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                dto = dto
            )
        ).thenReturn(dto)

        webClient.mutateWith(
            SecurityMockServerConfigurers.mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/templates")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
    }
}
