package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.entity.issue.Issue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class IssueMapperTest {

    @Test
    fun `should convert Issue to IssueDto`() {
        val issue = Issue(
            id = UUID.randomUUID(),
            jiraId = "10000",
            key = "SPP-10",
            link = "https://scrumpokerpro.atlassian.net/browse/SPP-10",
            title = "Import tasks",
            storyPoint = "13",
            synchronized = true,
            description = "Integration with Jira and searching issues",
            status = "NEW",
            type = "Bug",
            parentId = UUID.randomUUID(),
            meetingId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )

        val issueDto = issue.toDto()
        assertEquals(issue.id, issueDto.id)
        assertEquals(issue.jiraId, issueDto.jiraId)
        assertEquals(issue.key, issueDto.key)
        assertEquals(issue.link, issueDto.link)
        assertEquals(issue.title, issueDto.title)
        assertEquals(issue.storyPoint, issueDto.storyPoint)
        assertEquals(issue.synchronized, issueDto.synchronized)
        assertEquals(issue.description, issueDto.description)
        assertEquals(issue.status, issueDto.status)
        assertEquals(issue.type, issueDto.type)
        assertEquals(issue.parentId, issueDto.parentId)
        assertEquals(issue.created, issueDto.created)
        assertEquals(issue.modified, issueDto.modified)
    }

    @Test
    fun `should convert JiraIssueDto to Issue`() {
        val jiraIssueDto = JiraIssueDto(
            id = "10000",
            key = "SPP-10",
            link = "https://scrumpokerpro.atlassian.net/browse/SPP-10",
            status = "NEW",
            title = "Jira integration",
            type = "Story"
        )
        val userId = UUID.randomUUID()
        val meetingId = UUID.randomUUID()
        val issue = jiraIssueDto.toEntity(
            userId = userId,
            meetingId = meetingId
        )

        assertEquals(jiraIssueDto.key, issue.key)
        assertEquals(jiraIssueDto.link, issue.link)
        assertEquals(jiraIssueDto.title, issue.title)
        assertEquals(userId, issue.userId)
        assertEquals(meetingId, issue.meetingId)
        assertEquals(jiraIssueDto.status, issue.status)
        assertEquals(jiraIssueDto.type, issue.type)
    }
}
