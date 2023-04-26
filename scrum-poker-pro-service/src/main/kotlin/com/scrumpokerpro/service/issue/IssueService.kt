package com.scrumpokerpro.service.issue

import com.scrumpokerpro.dto.issue.CreateIssueDto
import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateIssueDto
import com.scrumpokerpro.dto.issue.UpdateStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateVotingStatusDto
import com.scrumpokerpro.entity.issue.Issue
import java.util.UUID

@SuppressWarnings("TooManyFunctions")
interface IssueService {

    suspend fun importFromJira(userId: UUID, meetingId: UUID, issuesDto: List<JiraIssueDto>): List<IssueDto>

    suspend fun importFromMeeting(userId: UUID, meetingId: UUID, fromMeetingId: UUID): List<IssueDto>

    suspend fun updateStoryPoint(userId: UUID, issueId: UUID, updateStoryPointDto: UpdateStoryPointDto): IssueDto

    suspend fun updateVotingStatus(userId: UUID, issueId: UUID, updateVotingStatusDto: UpdateVotingStatusDto): IssueDto

    suspend fun syncStoryPoint(userId: UUID, token: String, issueId: UUID, syncStoryPointDto: SyncStoryPointDto): IssueDto

    suspend fun syncStoryPoints(userId: UUID, token: String, meetingId: UUID, syncStoryPointDto: SyncStoryPointDto): List<IssueDto>

    suspend fun getIssues(userId: UUID, meetingId: UUID): List<IssueDto>

    suspend fun getIssue(issueId: UUID): Issue?

    suspend fun createIssue(userId: UUID, meetingId: UUID, createIssueDto: CreateIssueDto): IssueDto

    suspend fun updateIssue(userId: UUID, issueId: UUID, updateIssueDto: UpdateIssueDto): IssueDto

    suspend fun importSubTasks(userId: UUID, token: String, issueId: UUID): List<IssueDto>

    suspend fun deleteIssue(userId: UUID, issueId: UUID)
}
