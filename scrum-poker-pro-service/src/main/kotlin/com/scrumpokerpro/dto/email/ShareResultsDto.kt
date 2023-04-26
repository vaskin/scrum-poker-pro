package com.scrumpokerpro.dto.email

import java.util.UUID

data class ShareResultsDto(
    val email: String? = null,
    val groupInviteId: UUID? = null,
    val meetingId: UUID
)
