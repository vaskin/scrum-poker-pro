package com.scrumpokerpro.service.vote

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.vote.CreateVoteDto
import com.scrumpokerpro.entity.issue.Issue
import com.scrumpokerpro.entity.issue.VotingStatus
import com.scrumpokerpro.entity.vote.Vote
import com.scrumpokerpro.repository.IssueRepository
import com.scrumpokerpro.repository.VoteRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class VoteServiceImplTest(
    @Autowired
    val voteService: VoteService,
    @Autowired
    val voteRepository: VoteRepository,
    @Autowired
    val issueRepository: IssueRepository
) {

    @Test
    fun `should create vote`() {
        runBlocking {
            voteRepository.deleteAll()
            val issueId = UUID.randomUUID()
            val userId = UUID.randomUUID()

            val voteId = voteService.createVote(
                userId = userId,
                issueId = issueId,
                createVoteDto = CreateVoteDto(storyPoint = "16")
            ).id

            val createdVote = voteRepository.findById(voteId)
            assertEquals("16", createdVote?.storyPoint)
            assertEquals(issueId, createdVote?.issueId)
            assertEquals(userId, createdVote?.userId)
            assertNotNull(createdVote?.created)
            assertNotNull(createdVote?.modified)
        }
    }

    @Test
    fun `should delete all votes by issueId`() {
        runBlocking {
            voteRepository.deleteAll()
            val issueId = UUID.randomUUID()
            voteRepository.save(
                Vote(
                    storyPoint = "16",
                    issueId = issueId,
                    userId = UUID.randomUUID(),
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )

            voteService.deleteByIssueId(issueId)

            assertNull(voteRepository.findAll().firstOrNull())
        }
    }

    @Test
    fun `should delete vote by issueId and userId`() {
        runBlocking {
            voteRepository.deleteAll()
            val issueId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            voteRepository.save(
                Vote(
                    storyPoint = "16",
                    issueId = issueId,
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )

            voteService.deleteVote(userId, issueId)

            assertNull(voteRepository.findAll().firstOrNull())
        }
    }

    @Test
    fun `should get votes list by issueId`() {
        runBlocking {
            voteRepository.deleteAll()
            val issueId = Issue(
                title = "title",
                storyPoint = "1",
                meetingId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                type = "Story",
                votingStatus = VotingStatus.DONE,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                issueRepository.save(it)
            }.id!!
            val vote = Vote(
                storyPoint = "16",
                issueId = issueId,
                userId = UUID.randomUUID(),
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                voteRepository.save(it)
            }

            val votes = voteService.getVotes(issueId, vote.userId)

            assertEquals(vote.storyPoint, votes[0].storyPoint)
            assertEquals(issueId, votes[0].issueId)
            assertEquals(vote.userId, votes[0].userId)
            assertNotNull(votes[0].created)
            assertNotNull(votes[0].modified)
        }
    }
}
