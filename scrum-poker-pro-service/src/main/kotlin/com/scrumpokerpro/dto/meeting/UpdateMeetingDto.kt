package com.scrumpokerpro.dto.meeting

import java.util.UUID

data class UpdateMeetingDto(
    val name: String? = null,
    val currentIssueId: UUID? = null
)
