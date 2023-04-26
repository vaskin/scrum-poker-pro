package com.scrumpokerpro.mapper

import com.scrumpokerpro.dto.profile.ParticipantDto
import com.scrumpokerpro.entity.participant.Participant

fun Participant.toDto(avatar: String? = null, contentType: String? = null, fileName: String? = null) = ParticipantDto(
    userId = userId,
    name = name,
    avatar = avatar,
    contentType = contentType,
    fileName = fileName,
    lastActivity = lastActivity,
    created = created,
    modified = modified
)
