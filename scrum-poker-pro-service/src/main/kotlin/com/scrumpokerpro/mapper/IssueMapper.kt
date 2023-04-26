package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.issue.IssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.entity.issue.Issue
import java.time.LocalDateTime
import java.util.UUID

fun JiraIssueDto.toEntity(
    userId: UUID,
    meetingId: UUID,
    now: LocalDateTime = LocalDateTime.now(),
    issueParentId: UUID? = null
) = Issue(
    jiraId = id,
    jiraParentId = parentId,
    key = key,
    link = link,
    title = title,
    description = description,
    status = status,
    type = type,
    iconUrl = iconUrl,
    subtask = subtask,
    meetingId = meetingId,
    userId = userId,
    parentId = issueParentId,
    created = now,
    modified = now
)

fun Issue.toDto() = IssueDto(
    id = id,
    jiraId = jiraId,
    key = key,
    link = link,
    title = title,
    description = description,
    storyPoint = storyPoint,
    status = status,
    type = type,
    iconUrl = iconUrl,
    subtask = subtask,
    parentId = parentId,
    synchronized = synchronized,
    votingStatus = votingStatus,
    created = created,
    modified = modified
)
