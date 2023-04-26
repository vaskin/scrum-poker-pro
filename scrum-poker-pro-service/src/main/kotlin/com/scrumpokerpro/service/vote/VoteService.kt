package com.scrumpokerpro.service.vote

import com.scrumpokerpro.dto.vote.CreateVoteDto
import com.scrumpokerpro.dto.vote.VoteDto
import java.util.UUID

interface VoteService {

    suspend fun deleteByIssueId(issueId: UUID)

    suspend fun deleteVote(userId: UUID, issueId: UUID)

    suspend fun createVote(userId: UUID, issueId: UUID, createVoteDto: CreateVoteDto): VoteDto

    suspend fun getVotes(issueId: UUID, userId: UUID): List<VoteDto>
}
