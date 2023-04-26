package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.issue.CreateIssueDto
import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.meeting.CreateMeetingDto
import com.scrumpokerpro.dto.meeting.MeetingDto
import com.scrumpokerpro.dto.meeting.UpdateMeetingDto
import com.scrumpokerpro.dto.profile.ParticipantDto
import com.scrumpokerpro.service.issue.IssueService
import com.scrumpokerpro.service.meeting.MeetingService
import com.scrumpokerpro.service.participant.ParticipantService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.userId
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/meetings")
@SuppressWarnings("TooManyFunctions")
class MeetingController(
    val meetingService: MeetingService,
    val issueService: IssueService,
    val participantService: ParticipantService
) {

    val log by logger()

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun getMeetings(@AuthenticationPrincipal principal: Jwt): List<MeetingDto> {
        return meetingService.getMeetings(principal.userId())
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    suspend fun getMeeting(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): MeetingDto {
        return meetingService.getMeeting(id, principal.userId())
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun createMeeting(
        @RequestBody dto: CreateMeetingDto,
        @AuthenticationPrincipal principal: Jwt
    ): MeetingDto {
        log.info("create meeting [userId = {}, dto = {}]", principal.userId(), dto)
        return meetingService.createMeeting(
            name = principal.getClaimAsString("name"),
            userId = principal.userId(),
            dto = dto
        )
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateMeeting(
        @PathVariable id: UUID,
        @RequestBody dto: UpdateMeetingDto,
        @AuthenticationPrincipal principal: Jwt
    ): MeetingDto {
        log.info("update meeting [id = {}, userId = {}, dto = {}]", id, principal.userId(), dto)
        return meetingService.updateMeeting(id, principal.userId(), dto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteMeeting(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: Jwt
    ) {
        log.info("delete meeting [id = {}, userId = {}]", id, principal.userId())
        return meetingService.deleteMeeting(id, principal.userId())
    }

    @PostMapping("/{meetingId}/issues")
    @PreAuthorize("hasRole('USER')")
    suspend fun createIssue(
        @PathVariable meetingId: UUID,
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody createIssueDto: CreateIssueDto
    ): IssueDto {
        log.info("create issue [meetingId = {}, userId = {}, createIssueDto = {}]", meetingId, principal.userId(), createIssueDto)
        return issueService.createIssue(principal.userId(), meetingId, createIssueDto)
    }

    @GetMapping("/{meetingId}/issues")
    @PreAuthorize("hasRole('USER')")
    suspend fun getIssues(
        @PathVariable meetingId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): List<IssueDto> {
        return issueService.getIssues(principal.userId(), meetingId)
    }

    @PostMapping("/{meetingId}/jira-issues")
    @PreAuthorize("hasRole('USER')")
    suspend fun import(
        @PathVariable meetingId: UUID,
        @RequestBody issuesDto: List<JiraIssueDto>,
        @AuthenticationPrincipal principal: Jwt
    ): List<IssueDto> {
        log.info("import issues [userId = {}, issuesDto = {}]", principal.userId(), issuesDto)
        val issues = issueService.importFromJira(
            userId = principal.userId(),
            meetingId = meetingId,
            issuesDto = issuesDto
        )
        log.info("{} issues imported [userId = {}]", issues.size, principal.userId())
        return issues
    }

    @PostMapping("/{meetingId}/copy-issues/{fromMeetingId}")
    @PreAuthorize("hasRole('USER')")
    suspend fun importFromMeeting(
        @PathVariable meetingId: UUID,
        @PathVariable fromMeetingId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): List<IssueDto> {
        log.info("import issues from meeting [userId = {}, meetingId = {}, fromMeetingId = {}]", principal.userId(), meetingId, fromMeetingId)
        return issueService.importFromMeeting(
            userId = principal.userId(),
            meetingId = meetingId,
            fromMeetingId = fromMeetingId
        )
    }

    @GetMapping("/{meetingId}/participants")
    @PreAuthorize("hasRole('USER')")
    suspend fun getParticipants(
        @PathVariable meetingId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): List<ParticipantDto> {
        return participantService.getParticipantsDto(meetingId)
    }

    @PostMapping("/{meetingId}/participants")
    @PreAuthorize("hasRole('USER')")
    suspend fun createParticipant(
        @PathVariable meetingId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ) {
        log.info("create participant [meetingId = {}, userId = {}]", meetingId, principal.userId())
        return participantService.createParticipant(
            meetingId = meetingId,
            name = principal.getClaimAsString("name"),
            userId = principal.userId()
        )
    }

    @PutMapping("/{meetingId}/story-points/sync")
    @PreAuthorize("hasRole('USER')")
    suspend fun syncStoryPoints(
        @PathVariable meetingId: UUID,
        @RequestBody syncStoryPointDto: SyncStoryPointDto,
        @AuthenticationPrincipal principal: Jwt
    ): List<IssueDto> {
        log.info("sync story points [userId = {}, meetingId = {}, syncStoryPointDto = {}]", principal.userId(), meetingId, syncStoryPointDto)
        return issueService.syncStoryPoints(
            userId = principal.userId(),
            token = principal.tokenValue,
            meetingId = meetingId,
            syncStoryPointDto = syncStoryPointDto
        )
    }
}
