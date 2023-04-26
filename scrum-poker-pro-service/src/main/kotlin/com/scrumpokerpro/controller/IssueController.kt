package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateIssueDto
import com.scrumpokerpro.dto.issue.UpdateStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateVotingStatusDto
import com.scrumpokerpro.dto.vote.CreateVoteDto
import com.scrumpokerpro.dto.vote.VoteDto
import com.scrumpokerpro.service.issue.IssueService
import com.scrumpokerpro.service.vote.VoteService
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
@RequestMapping("/issues")
class IssueController(
    val issueService: IssueService,
    val voteService: VoteService
) {

    val log by logger()

    @PutMapping("/{issueId}/story-points")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateStoryPoint(
        @PathVariable issueId: UUID,
        @RequestBody updateStoryPointDto: UpdateStoryPointDto,
        @AuthenticationPrincipal principal: Jwt
    ): IssueDto {
        log.info("update story points [userId = {}, issueId = {}, updateStoryPointDto = {}]", principal.userId(), issueId, updateStoryPointDto)
        return issueService.updateStoryPoint(
            userId = principal.userId(),
            issueId = issueId,
            updateStoryPointDto = updateStoryPointDto
        )
    }

    @PutMapping("/{issueId}/story-points/sync")
    @PreAuthorize("hasRole('USER')")
    suspend fun syncStoryPoint(
        @PathVariable issueId: UUID,
        @RequestBody syncStoryPointDto: SyncStoryPointDto,
        @AuthenticationPrincipal principal: Jwt
    ): IssueDto {
        log.info("sync story points [userId = {}, issueId = {}, syncStoryPointDto = {}]", principal.userId(), issueId, syncStoryPointDto)
        return issueService.syncStoryPoint(
            userId = principal.userId(),
            token = principal.tokenValue,
            issueId = issueId,
            syncStoryPointDto = syncStoryPointDto
        )
    }

    @PutMapping("/{issueId}/voting-status")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateVotingStatus(
        @PathVariable issueId: UUID,
        @RequestBody updateVotingStatusDto: UpdateVotingStatusDto,
        @AuthenticationPrincipal principal: Jwt
    ): IssueDto {
        log.info("update voting points [userId = {}, issueId = {}, updateVotingStatusDto = {}]", principal.userId(), issueId, updateVotingStatusDto)
        return issueService.updateVotingStatus(
            userId = principal.userId(),
            issueId = issueId,
            updateVotingStatusDto = updateVotingStatusDto
        )
    }

    @GetMapping("/{issueId}/votes")
    @PreAuthorize("hasRole('USER')")
    suspend fun getVotes(
        @PathVariable issueId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): List<VoteDto> {
        log.info("get votes [issueId = {}]", issueId)
        return voteService.getVotes(issueId = issueId, userId = principal.userId())
    }

    @PostMapping("/{issueId}/votes")
    @PreAuthorize("hasRole('USER')")
    suspend fun createVote(
        @PathVariable issueId: UUID,
        @RequestBody createVoteDto: CreateVoteDto,
        @AuthenticationPrincipal principal: Jwt
    ): VoteDto {
        log.info("create vote [userId = {}, issueId = {}, createVoteDto = {}]", principal.userId(), issueId, createVoteDto)
        return voteService.createVote(
            userId = principal.userId(),
            issueId = issueId,
            createVoteDto = createVoteDto
        )
    }

    @DeleteMapping("/{issueId}/votes")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteVote(
        @PathVariable issueId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ) {
        log.info("delete vote [userId = {}, issueId = {}]", principal.userId(), issueId)
        voteService.deleteVote(userId = principal.userId(), issueId = issueId)
    }

    @PostMapping("/{issueId}/sub-tasks")
    @PreAuthorize("hasRole('USER')")
    suspend fun importSubTasks(
        @PathVariable issueId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ): List<IssueDto> {
        principal.tokenValue
        log.info("import sub-tasks [userId = {}, issueId = {}]", principal.userId(), issueId)
        return issueService.importSubTasks(userId = principal.userId(), token = principal.tokenValue, issueId = issueId)
    }

    @DeleteMapping("/{issueId}")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteIssues(
        @PathVariable issueId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ) {
        log.info("delete issue [userId = {}, issueId = {}]", principal.userId(), issueId)
        issueService.deleteIssue(userId = principal.userId(), issueId = issueId)
    }

    @PutMapping("/{issueId}")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateIssue(
        @PathVariable issueId: UUID,
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody updateIssueDto: UpdateIssueDto
    ): IssueDto {
        log.info("update issue [issueId = {}, userId = {}, updateIssueDto = {}]", issueId, principal.userId(), updateIssueDto)
        return issueService.updateIssue(principal.userId(), issueId, updateIssueDto)
    }
}
