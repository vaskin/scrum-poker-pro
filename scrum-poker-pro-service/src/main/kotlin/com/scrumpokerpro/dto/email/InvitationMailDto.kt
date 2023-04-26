package com.scrumpokerpro.dto.email

import java.util.UUID

data class InvitationMailDto(
    val email: String? = null,
    val groupInviteId: UUID? = null,
    val meetingId: UUID
)
