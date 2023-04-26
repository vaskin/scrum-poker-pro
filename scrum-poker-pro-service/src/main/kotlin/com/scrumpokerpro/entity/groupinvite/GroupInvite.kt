package com.scrumpokerpro.entity.groupinvite

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class GroupInvite(
    @Id
    val id: UUID? = null,
    val name: String,
    val userId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
