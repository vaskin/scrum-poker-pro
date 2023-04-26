package com.scrumpokerpro.entity.meeting

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Meeting(
    @Id
    val id: UUID? = null,
    val name: String,
    val type: MeetingType,
    val votingSystem: MeetingVotingSystem? = null,
    val userId: UUID,
    val currentIssueId: UUID? = null,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
