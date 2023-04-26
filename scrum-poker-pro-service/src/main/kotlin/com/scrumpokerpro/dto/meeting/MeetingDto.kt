package com.scrumpokerpro.dto.meeting

import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.entity.meeting.MeetingVotingSystem
import java.time.LocalDateTime
import java.util.UUID

data class MeetingDto(
    val id: UUID,
    val name: String,
    val type: MeetingType,
    val votingSystem: MeetingVotingSystem? = null,
    val userId: UUID,
    val currentIssueId: UUID?,
    var columns: List<RetroColumnDto> = listOf(),
    val created: LocalDateTime,
    val modified: LocalDateTime
)
