package com.scrumpokerpro.dto.groupinvite

import java.util.UUID

data class GroupInviteDto(
    val id: UUID,
    val name: String,
    val emails: List<String>
)
