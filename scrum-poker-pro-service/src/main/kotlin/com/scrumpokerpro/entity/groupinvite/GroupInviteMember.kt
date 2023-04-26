package com.scrumpokerpro.entity.groupinvite

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class GroupInviteMember(
    @Id
    val id: UUID? = null,
    val email: String,
    val groupInviteId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
