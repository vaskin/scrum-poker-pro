package com.scrumpokerpro.controller

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.service.profile.ProfileService
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class ProfileControllerTest(
    @Autowired
    val webClient: WebTestClient,
) {
    @MockBean
    lateinit var profileService: ProfileService

    @Test
    fun `should update profiles`() = runBlockingTest {
        val builder = MultipartBodyBuilder()
        builder.part("name", "Alex")
        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/profiles")
            .bodyValue(builder.build())
            .exchange()
            .expectStatus().isOk
    }
}
