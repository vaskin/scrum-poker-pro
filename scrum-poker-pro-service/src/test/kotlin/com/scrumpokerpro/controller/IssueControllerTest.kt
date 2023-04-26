package com.scrumpokerpro.controller

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.UpdateIssueDto
import com.scrumpokerpro.dto.issue.UpdateStoryPointDto
import com.scrumpokerpro.service.issue.IssueService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class IssueControllerTest(
    @Autowired
    val webClient: WebTestClient,
) {
    @MockBean
    lateinit var issueService: IssueService

    @ExperimentalCoroutinesApi
    @Test
    fun `should update story point on issue`() = runBlockingTest {
        val updateStoryPointDto = UpdateStoryPointDto("13")
        val issueDto = IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())
        `when`(
            issueService.updateStoryPoint(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                issueId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                updateStoryPointDto = updateStoryPointDto
            )
        ).thenReturn(issueDto)

        val response = webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/issues/2bb626ae-7707-4278-9b69-8fdba13e4a6c/story-points")
            .bodyValue(updateStoryPointDto)
            .exchange()
            .expectStatus().isOk
            .expectBody(IssueDto::class.java)
            .returnResult().responseBody
        assertEquals(issueDto.title, response?.title)
        assertEquals(issueDto.type, response?.type)
        assertEquals(issueDto.created, response?.created)
        assertEquals(issueDto.modified, response?.modified)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should import sub-tasks`() = runBlockingTest {
        val issueDto = IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())
        `when`(
            issueService.importSubTasks(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                token = "token",
                issueId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c")
            )
        ).thenReturn(mutableListOf(issueDto))

        val response = webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/issues/2bb626ae-7707-4278-9b69-8fdba13e4a6c/sub-tasks")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(IssueDto::class.java)
            .returnResult().responseBody
        assertEquals(issueDto.title, response?.get(0)?.title)
        assertEquals(issueDto.type, response?.get(0)?.type)
        assertEquals(issueDto.created, response?.get(0)?.created)
        assertEquals(issueDto.modified, response?.get(0)?.modified)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should delete issue`() = runBlockingTest {
        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri("/issues/2bb626ae-7707-4278-9b69-8fdba13e4a6c")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(IssueDto::class.java)
            .returnResult().responseBody

        verify(issueService).deleteIssue(
            UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
            UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c")
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should update issue`() = runBlockingTest {
        val updateIssueDto = UpdateIssueDto(
            title = "Create jira service",
            description = "Import task from Jira",
            type = "Task"
        )
        `when`(
            issueService.updateIssue(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                issueId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                updateIssueDto = updateIssueDto
            )
        ).thenReturn(IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now()))

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/issues/2bb626ae-7707-4278-9b69-8fdba13e4a6c")
            .bodyValue(updateIssueDto)
            .exchange()
            .expectStatus().isOk
    }
}
