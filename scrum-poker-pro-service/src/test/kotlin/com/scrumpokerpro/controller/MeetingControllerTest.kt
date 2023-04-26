package com.scrumpokerpro.controller

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.issue.CreateIssueDto
import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.profile.ParticipantDto
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.repository.MeetingRepository
import com.scrumpokerpro.service.issue.IssueService
import com.scrumpokerpro.service.meeting.MeetingService
import com.scrumpokerpro.service.participant.ParticipantService
import com.scrumpokerpro.service.retrotemplate.RetroTemplateService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
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
class MeetingControllerTest(
    @Autowired
    val webClient: WebTestClient,
) {
    @MockBean
    lateinit var meetingService: MeetingService

    @MockBean
    lateinit var issueService: IssueService

    @MockBean
    lateinit var participantService: ParticipantService

    @MockBean
    lateinit var meetingRepository: MeetingRepository

    @MockBean
    lateinit var retroTemplateService: RetroTemplateService

    @ExperimentalCoroutinesApi
    @Test
    fun `should return 404 meeting not found`() = runBlockingTest {
        `when`(
            meetingService.getMeeting(
                UUID.fromString("3ad37bbe-5a7b-4d6e-b6dc-1b9899eeec7b"),
                UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803")
            )
        ).thenThrow(EntityNotFoundException())

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).get()
            .uri("/meetings/3ad37bbe-5a7b-4d6e-b6dc-1b9899eeec7b")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.type").isEqualTo("/resource/not_found")
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.title").isEqualTo("Resource not found")
            .jsonPath("$.detail").isEqualTo("")
            .jsonPath("$.path").isEqualTo("/meetings/3ad37bbe-5a7b-4d6e-b6dc-1b9899eeec7b")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return 403 access denied`() = runBlockingTest {
        `when`(
            meetingService.getMeeting(
                UUID.fromString("3ad37bbe-5a7b-4d6e-b6dc-1b9899eeec7b"),
                UUID.fromString("94c27b56-aad4-47a4-a893-43a44bade80a")
            )
        ).thenThrow(AccessDeniedException(""))

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("94c27b56-aad4-47a4-a893-43a44bade80a")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).get()
            .uri("/meetings/3ad37bbe-5a7b-4d6e-b6dc-1b9899eeec7b")
            .exchange()
            .expectStatus().isForbidden
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should get issues`() = runBlockingTest {
        val issueDto = IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())
        `when`(
            issueService.getIssues(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
            )
        ).thenReturn(listOf(issueDto))

        val response = webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .get()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/issues")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(IssueDto::class.java)
            .returnResult().responseBody
        assertTrue(response?.size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should import issues`() = runBlockingTest {
        val issues = listOf(
            JiraIssueDto(
                id = "10000",
                key = "SPP-10",
                link = "https://scrumpokerpro.atlassian.net/browse/SPP-10",
                status = "NEW",
                title = "Jira integration",
                type = "Story"
            )
        )
        `when`(
            issueService.importFromJira(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                issuesDto = issues
            )
        ).thenReturn(listOf(IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())))

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/jira-issues")
            .bodyValue(issues)
            .exchange()
            .expectStatus().isOk
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should get participants`() = runBlockingTest {
        val participantDto = ParticipantDto(
            userId = UUID.randomUUID(),
            name = "Josh",
            lastActivity = LocalDateTime.now(),
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )
        `when`(
            participantService.getParticipantsDto(
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
            )
        ).thenReturn(listOf(participantDto))

        val response = webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .get()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/participants")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ParticipantDto::class.java)
            .returnResult().responseBody
        assertTrue(response?.size == 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should create participant`() = runBlockingTest {
        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
                jwt.claim("name", "Peter")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/participants")
            .exchange()
            .expectStatus().isOk

        verify(participantService).createParticipant(
            meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
            name = "Peter",
            userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803")
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should create issue`() = runBlockingTest {
        val createIssueDto = CreateIssueDto(
            title = "Create jira service",
            description = "Import task from Jira",
            type = "Task"
        )
        `when`(
            issueService.createIssue(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                createIssueDto = createIssueDto
            )
        ).thenReturn(IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now()))

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/issues")
            .bodyValue(createIssueDto)
            .exchange()
            .expectStatus().isOk
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should import issues from meeting`() = runBlockingTest {
        `when`(
            issueService.importFromMeeting(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                fromMeetingId = UUID.fromString("e97a8a94-10ad-42a8-8f52-4b01c2daa884")
            )
        ).thenReturn(listOf(IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())))

        webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/copy-issues/e97a8a94-10ad-42a8-8f52-4b01c2daa884")
            .exchange()
            .expectStatus().isOk
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should sync story point with Jira for all issues in meeting`() = runBlockingTest {
        `when`(
            issueService.syncStoryPoints(
                userId = UUID.fromString("9539672c-2cef-41dc-b608-9d70db230803"),
                token = "token",
                meetingId = UUID.fromString("2bb626ae-7707-4278-9b69-8fdba13e4a6c"),
                syncStoryPointDto = SyncStoryPointDto(fieldId = "customfield_10026")
            )
        ).thenReturn(listOf(IssueDto(title = "title", type = "type", created = LocalDateTime.now(), modified = LocalDateTime.now())))

        val response = webClient.mutateWith(
            mockJwt().jwt { jwt ->
                jwt.subject("9539672c-2cef-41dc-b608-9d70db230803")
            }.authorities(SimpleGrantedAuthority("ROLE_USER"))
        ).mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/meetings/2bb626ae-7707-4278-9b69-8fdba13e4a6c/story-points/sync")
            .bodyValue(SyncStoryPointDto(fieldId = "customfield_10026"))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(IssueDto::class.java)
            .returnResult().responseBody
        assertEquals(1, response?.size)
    }
}
