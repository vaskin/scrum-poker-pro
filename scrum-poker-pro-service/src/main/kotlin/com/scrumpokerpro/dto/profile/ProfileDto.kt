package com.scrumpokerpro.dto.profile

import java.util.UUID

data class ProfileDto(
    val userId: UUID,
    val name: String,
    val avatar: String? = null,
    val contentType: String? = null,
    val fileName: String? = null
)
