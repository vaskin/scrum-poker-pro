package com.scrumpokerpro.service.vote

import com.scrumpokerpro.dto.vote.CreateVoteDto
import com.scrumpokerpro.dto.vote.VoteDto
import com.scrumpokerpro.entity.issue.VotingStatus
import com.scrumpokerpro.entity.vote.Vote
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.repository.VoteRepository
import com.scrumpokerpro.service.issue.IssueService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class VoteServiceImpl(
    val voteRepository: VoteRepository,
    @Lazy
    val issueService: IssueService
) : VoteService {

    @Transactional
    override suspend fun deleteByIssueId(issueId: UUID) {
        voteRepository.deleteAllByIssueId(issueId)
    }

    @Transactional
    override suspend fun createVote(userId: UUID, issueId: UUID, createVoteDto: CreateVoteDto): VoteDto {
        val now = LocalDateTime.now()
        val vote = voteRepository.findByUserIdAndIssueId(userId = userId, issueId = issueId)?.let {
            voteRepository.save(
                it.copy(
                    storyPoint = createVoteDto.storyPoint,
                    modified = LocalDateTime.now()
                )
            )
        } ?: voteRepository.save(
            Vote(
                storyPoint = createVoteDto.storyPoint,
                issueId = issueId,
                userId = userId,
                created = now,
                modified = now
            )
        )
        return vote.toDto(VotingStatus.NEW, userId)
    }

    @Transactional
    override suspend fun deleteVote(userId: UUID, issueId: UUID) {
        voteRepository.deleteByUserIdAndIssueId(userId = userId, issueId = issueId)
    }

    override suspend fun getVotes(issueId: UUID, userId: UUID): List<VoteDto> {
        val issue = issueService.getIssue(issueId) ?: throw EntityNotFoundException()
        val votes = voteRepository.findByIssueId(issueId)
        return votes.map { it.toDto(issue.votingStatus, userId) }
    }
}
