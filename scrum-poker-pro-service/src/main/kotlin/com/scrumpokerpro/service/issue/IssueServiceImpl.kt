package com.scrumpokerpro.service.issue

import com.scrumpokerpro.dto.issue.CreateIssueDto
import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateIssueDto
import com.scrumpokerpro.dto.issue.UpdateJiraIssueDto
import com.scrumpokerpro.dto.issue.UpdateStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateVotingStatusDto
import com.scrumpokerpro.entity.issue.Issue
import com.scrumpokerpro.entity.issue.VotingStatus
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.mapper.toEntity
import com.scrumpokerpro.repository.IssueRepository
import com.scrumpokerpro.repository.MeetingRepository
import com.scrumpokerpro.service.jira.ScrumPokerProJiraClient
import com.scrumpokerpro.service.participant.ParticipantService
import com.scrumpokerpro.service.vote.VoteService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@SuppressWarnings("TooManyFunctions")
class IssueServiceImpl(
    val issueRepository: IssueRepository,
    val meetingRepository: MeetingRepository,
    val scrumPokerProJiraClient: ScrumPokerProJiraClient,
    val voteService: VoteService,
    val participantService: ParticipantService,
) : IssueService {

    @Transactional
    override suspend fun importFromJira(userId: UUID, meetingId: UUID, issuesDto: List<JiraIssueDto>): List<IssueDto> {
        meetingRepository.findById(meetingId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        return issuesDto.filter {
            issueRepository.findByJiraIdAndMeetingId(jiraId = it.id, meetingId = meetingId) == null
        }.map {
            val parentId = if (it.parentId != null) {
                issueRepository.findByJiraIdAndMeetingId(jiraId = it.parentId, meetingId = meetingId)?.id
            } else null
            issueRepository.save(it.toEntity(userId = userId, meetingId = meetingId, issueParentId = parentId)).toDto()
        }
    }

    override suspend fun importFromMeeting(userId: UUID, meetingId: UUID, fromMeetingId: UUID): List<IssueDto> {
        meetingRepository.findById(meetingId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        val now = LocalDateTime.now()
        val existingIssues = issueRepository.findByMeetingIdAndUserId(meetingId, userId)
        val meetingIssues = issueRepository.findByMeetingIdAndUserId(fromMeetingId, userId).filterNot {
            existingIssues.map { existingIssue -> existingIssue.fromIssueId }.contains(it.id)
        }
        return meetingIssues.map {
            issueRepository.save(
                it.copy(
                    id = null,
                    meetingId = meetingId,
                    fromIssueId = it.id,
                    votingStatus = VotingStatus.NEW,
                    created = now,
                    modified = now
                )
            ).toDto()
        }
    }

    @Transactional
    override suspend fun deleteIssue(userId: UUID, issueId: UUID) {
        issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        issueRepository.findByParentId(issueId).forEach {
            issueRepository.delete(it)
        }
        issueRepository.deleteById(issueId)
    }

    @Transactional
    override suspend fun updateStoryPoint(userId: UUID, issueId: UUID, updateStoryPointDto: UpdateStoryPointDto): IssueDto {
        val issue = issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        return issueRepository.save(
            issue.copy(
                storyPoint = updateStoryPointDto.storyPoint,
                synchronized = issue.jiraId == null,
                modified = LocalDateTime.now()
            )
        ).toDto()
    }

    override suspend fun getIssue(issueId: UUID): Issue? {
        return issueRepository.findById(issueId)
    }

    @Transactional
    override suspend fun updateVotingStatus(userId: UUID, issueId: UUID, updateVotingStatusDto: UpdateVotingStatusDto): IssueDto {
        val issue = issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        if (updateVotingStatusDto.votingStatus == VotingStatus.NEW) {
            voteService.deleteByIssueId(issueId)
        }
        return issueRepository.save(
            issue.copy(
                votingStatus = updateVotingStatusDto.votingStatus,
                modified = LocalDateTime.now()
            )
        ).toDto()
    }

    @Transactional
    override suspend fun createIssue(userId: UUID, meetingId: UUID, createIssueDto: CreateIssueDto): IssueDto {
        meetingRepository.findById(meetingId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        val now = LocalDateTime.now()
        return Issue(
            title = createIssueDto.title,
            description = createIssueDto.description,
            link = createIssueDto.link,
            type = createIssueDto.type,
            userId = userId,
            meetingId = meetingId,
            created = now,
            modified = now
        ).let {
            issueRepository.save(it)
        }.toDto()
    }

    @Transactional
    override suspend fun importSubTasks(userId: UUID, token: String, issueId: UUID): List<IssueDto> {
        var position: Long = 0
        val now = LocalDateTime.now()
        val issue = issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        val subTasks = issueRepository.findByParentId(issueId)
        val jiraTasks = scrumPokerProJiraClient.getIssues(
            searchIssue = mapOf("text" to "parent = '${issue.key}' ORDER BY rank ASC", "jql" to true),
            token = "Bearer $token"
        ).collectList().awaitFirst()
        subTasks.forEach { subTask ->
            val jiraIds = jiraTasks.map { it.id }
            if (!jiraIds.contains(subTask.jiraId)) {
                issueRepository.delete(subTask)
            }
        }
        return jiraTasks.filter {
            issueRepository.findByJiraIdAndMeetingId(jiraId = it.id, meetingId = issue.meetingId) == null
        }.mapNotNull {
            issueRepository.save(
                it.toEntity(
                    userId = userId,
                    meetingId = issue.meetingId,
                    issueParentId = issueId,
                    now = now.plusSeconds(++position)
                )
            ).toDto()
        }
    }

    @Transactional
    override suspend fun syncStoryPoint(userId: UUID, token: String, issueId: UUID, syncStoryPointDto: SyncStoryPointDto): IssueDto {
        val issue = issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        issue.jiraId?.let {
            scrumPokerProJiraClient.updateIssue(
                updateJiraIssue = UpdateJiraIssueDto(
                    issueId = issue.jiraId,
                    fieldId = syncStoryPointDto.fieldId,
                    schema = syncStoryPointDto.schema,
                    storyPoints = issue.storyPoint!!
                ),
                token = "Bearer $token"
            ).awaitFirstOrNull()
        }

        return issueRepository.save(issue.copy(synchronized = true)).toDto()
    }

    @Transactional
    override suspend fun syncStoryPoints(userId: UUID, token: String, meetingId: UUID, syncStoryPointDto: SyncStoryPointDto): List<IssueDto> {
        val issues = issueRepository.findByMeetingIdAndUserId(meetingId = meetingId, userId = userId)
        return issues.filterNot { it.synchronized }.map {
            syncStoryPoint(userId = userId, token = token, issueId = it.id!!, syncStoryPointDto = syncStoryPointDto)
        }
    }

    @Transactional
    override suspend fun updateIssue(userId: UUID, issueId: UUID, updateIssueDto: UpdateIssueDto): IssueDto {
        val issue = issueRepository.findById(issueId)?.also { issue ->
            if (issue.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        return issueRepository.save(
            issue.copy(
                title = updateIssueDto.title,
                description = updateIssueDto.description,
                link = updateIssueDto.link,
                type = updateIssueDto.type
            )
        ).toDto()
    }

    override suspend fun getIssues(userId: UUID, meetingId: UUID): List<IssueDto> {
        val isParticipant = participantService.getParticipants(meetingId).any { it.userId == userId }
        val issues = issueRepository.findByMeetingId(meetingId).filter {
            it.userId == userId || isParticipant
        }.sortedBy {
            it.created
        }.map {
            it.toDto()
        }
        return issues.filter { it.parentId == null }.map { parent ->
            parent.copy(child = issues.filter { child -> child.parentId == parent.id }.sortedBy { it.created })
        }
    }
}
