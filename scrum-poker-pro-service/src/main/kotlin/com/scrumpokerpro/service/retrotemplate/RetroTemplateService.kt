package com.scrumpokerpro.service.retrotemplate

import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import java.util.UUID

interface RetroTemplateService {

    suspend fun createTemplate(userId: UUID, dto: RetroTemplateDto): RetroTemplateDto

    suspend fun getTemplate(userId: UUID): RetroTemplateDto?
}
