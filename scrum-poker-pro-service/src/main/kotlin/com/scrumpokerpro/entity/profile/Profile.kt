package com.scrumpokerpro.entity.profile

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Profile(
    @Id
    val id: UUID? = null,
    val userId: UUID,
    val name: String,
    val avatarKey: UUID? = null,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
