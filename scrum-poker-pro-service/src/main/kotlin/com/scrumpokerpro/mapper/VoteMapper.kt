package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.vote.VoteDto
import com.scrumpokerpro.entity.issue.VotingStatus
import com.scrumpokerpro.entity.vote.Vote
import java.util.UUID

fun Vote.toDto(votingStatus: VotingStatus, currentUserId: UUID) = VoteDto(
    id = id!!,
    userId = userId,
    issueId = issueId,
    storyPoint = if (votingStatus == VotingStatus.DONE || userId == currentUserId) storyPoint else null,
    created = created,
    modified = modified
)
