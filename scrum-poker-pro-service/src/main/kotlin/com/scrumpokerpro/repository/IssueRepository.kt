package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.issue.Issue
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface IssueRepository : CoroutineCrudRepository<Issue, UUID> {

    suspend fun findByJiraIdAndMeetingId(jiraId: String, meetingId: UUID): Issue?

    suspend fun findByJiraParentId(jiraParentId: String): List<Issue>

    suspend fun findByMeetingIdAndUserId(meetingId: UUID, userId: UUID): List<Issue>

    suspend fun findByMeetingId(meetingId: UUID): List<Issue>

    suspend fun findByParentId(parentId: UUID): List<Issue>
}
