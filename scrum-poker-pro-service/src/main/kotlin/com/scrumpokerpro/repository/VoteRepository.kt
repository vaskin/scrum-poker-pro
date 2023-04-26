package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.vote.Vote
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface VoteRepository : CoroutineCrudRepository<Vote, UUID> {

    suspend fun deleteAllByIssueId(issueId: UUID)

    suspend fun deleteByUserIdAndIssueId(userId: UUID, issueId: UUID)

    suspend fun findByUserIdAndIssueId(userId: UUID, issueId: UUID): Vote?

    suspend fun findByIssueId(issueId: UUID): List<Vote>
}
