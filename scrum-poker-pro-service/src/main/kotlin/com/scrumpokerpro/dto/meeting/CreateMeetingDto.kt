package com.scrumpokerpro.dto.meeting

import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.entity.meeting.MeetingVotingSystem

data class CreateMeetingDto(
    val name: String,
    val type: MeetingType,
    val votingSystem: MeetingVotingSystem? = null,
    val columns: List<RetroColumnDto> = listOf()
)
