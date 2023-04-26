package com.scrumpokerpro.service.issue

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.issue.CreateIssueDto
import com.scrumpokerpro.dto.issue.JiraIssueDto
import com.scrumpokerpro.dto.issue.SyncStoryPointDto
import com.scrumpokerpro.dto.issue.UpdateIssueDto
import com.scrumpokerpro.dto.issue.UpdateStoryPointDto
import com.scrumpokerpro.entity.issue.Issue
import com.scrumpokerpro.entity.meeting.Meeting
import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.repository.IssueRepository
import com.scrumpokerpro.repository.MeetingRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest(
    properties = ["scrum-poker-pro.jira.url=http://localhost:\${wiremock.server.port}"]
)
@AutoConfigureWireMock(port = 0, stubs = ["classpath:/stubs"])
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class IssueServiceImplTest {

    @Autowired
    lateinit var issueService: IssueService

    @Autowired
    lateinit var meetingRepository: MeetingRepository

    @Autowired
    lateinit var issueRepository: IssueRepository

    @Test
    fun `should import 1 meeting`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val meeting = Meeting(
                name = "sprint planning",
                type = MeetingType.PLANNING,
                userId = userId,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                meetingRepository.save(it)
            }
            val issues = listOf(
                JiraIssueDto(
                    id = "11000",
                    key = "SPP-10",
                    link = "https://scrumpokerpro.atlassian.net/browse/SPP-10",
                    status = "NEW",
                    title = "Jira integration",
                    type = "Story"
                )
            )

            val importedIssues = issueService.importFromJira(
                userId = userId,
                meetingId = meeting.id!!,
                issuesDto = issues
            )
            val createdIssue = issueRepository.findById(importedIssues[0].id!!)
            assertEquals(issues[0].key, createdIssue?.key)
            assertEquals(issues[0].id, createdIssue?.jiraId)
            assertEquals(issues[0].description, createdIssue?.description)
            assertEquals(issues[0].link, createdIssue?.link)
            assertEquals(issues[0].status, createdIssue?.status)
            assertEquals(issues[0].title, createdIssue?.title)
            assertEquals(issues[0].type, createdIssue?.type)
        }
    }

    @Test
    fun `should throw AccessDeniedException because user is not able to access the meeting`() {
        runBlocking {
            val meeting = Meeting(
                name = "sprint planning",
                type = MeetingType.PLANNING,
                userId = UUID.randomUUID(),
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                meetingRepository.save(it)
            }

            assertThrows(AccessDeniedException::class.java) {
                runBlocking {
                    issueService.importFromJira(
                        userId = UUID.randomUUID(),
                        meetingId = meeting.id!!,
                        issuesDto = listOf()
                    )
                }
            }
        }
    }

    @Test
    fun `should set story point on issue`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val issue = Issue(
                title = "title",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            issueService.updateStoryPoint(
                userId = userId,
                issueId = issue.id!!,
                updateStoryPointDto = UpdateStoryPointDto("13")
            )
            val updatedIssue = issueRepository.findById(issue.id!!)
            assertEquals("13", updatedIssue?.storyPoint)
            assertTrue(updatedIssue?.synchronized!!)
        }
    }

    @Test
    fun `should sync story point with Jira`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val issue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            issueService.syncStoryPoint(
                userId = userId,
                token = "token",
                issueId = issue.id!!,
                syncStoryPointDto = SyncStoryPointDto(fieldId = "customfield_10026")
            )
            val updatedIssue = issueRepository.findById(issue.id!!)
            assertTrue(updatedIssue!!.synchronized)
        }
    }

    @Test
    fun `should get issues with child list`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val parentIssue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }
            val childIssue = Issue(
                title = "title",
                jiraId = "10001",
                storyPoint = "1",
                meetingId = parentIssue.meetingId,
                userId = userId,
                type = "Sub-task",
                parentId = parentIssue.id,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            val issues = issueService.getIssues(userId = userId, meetingId = parentIssue.meetingId)
            assertTrue(issues.size == 1)
            assertEquals(parentIssue.id, issues[0].id)
            assertEquals(childIssue.id, issues[0].child[0].id)
        }
    }

    @Test
    fun `should create issue`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val meeting = Meeting(
                name = "sprint planning",
                type = MeetingType.PLANNING,
                userId = userId,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                meetingRepository.save(it)
            }

            val issue = CreateIssueDto(
                title = "Create jira service",
                description = "Import task from Jira",
                type = "Task"
            ).let {
                issueService.createIssue(userId, meeting.id!!, it)
            }

            val createdIssue = issueRepository.findById(issue.id!!)!!
            assertEquals(issue.title, createdIssue.title)
            assertEquals(issue.description, createdIssue.description)
            assertEquals(issue.type, createdIssue.type)
        }
    }

    @Test
    fun `should update issue`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val issue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            val updateIssueDto = UpdateIssueDto(
                title = "Create jira service",
                link = "https://scrumpokerpro.com",
                description = "Import task from Jira",
                type = "Task"
            ).let {
                issueService.updateIssue(userId, issue.id!!, it)
            }

            val updatedIssue = issueRepository.findById(issue.id!!)!!
            assertEquals(updateIssueDto.title, updatedIssue.title)
            assertEquals(updateIssueDto.link, updatedIssue.link)
            assertEquals(updateIssueDto.description, updatedIssue.description)
            assertEquals(updateIssueDto.type, updatedIssue.type)
        }
    }

    @Test
    fun `should import sub-tasks`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val issue = Issue(
                title = "title",
                jiraId = "10000",
                key = "SPP-7",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }
            issueService.importSubTasks(userId, "token", issue.id!!)

            val createdIssues = issueRepository.findByJiraParentId("10015")
            assertEquals(2, createdIssues.size)
        }
    }

    @Test
    fun `should import tasks from meeting`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val fromMeetingId = UUID.randomUUID()
            val meeting = Meeting(
                name = "sprint planning",
                type = MeetingType.PLANNING,
                userId = userId,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                meetingRepository.save(it)
            }
            Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = fromMeetingId,
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }
            issueService.importFromMeeting(userId, meeting.id!!, fromMeetingId)

            val createdIssues = issueRepository.findByMeetingIdAndUserId(meeting.id!!, userId)
            assertEquals(1, createdIssues.size)
        }
    }

    @Test
    fun `should delete issue`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val parentIssue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }
            val childIssue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                parentId = parentIssue.id!!,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            issueService.deleteIssue(userId, parentIssue.id!!)
            assertNull(issueRepository.findById(parentIssue.id!!))
            assertNull(issueRepository.findById(childIssue.id!!))
        }
    }

    @Test
    fun `should sync story point with Jira for all issues in meeting`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val issue = Issue(
                title = "title",
                jiraId = "10000",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = userId,
                type = "Story",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }

            issueService.syncStoryPoints(
                userId = userId,
                token = "token",
                meetingId = issue.meetingId,
                syncStoryPointDto = SyncStoryPointDto(fieldId = "customfield_10026")
            )
            val updatedIssue = issueRepository.findById(issue.id!!)
            assertTrue(updatedIssue!!.synchronized)
        }
    }
}
