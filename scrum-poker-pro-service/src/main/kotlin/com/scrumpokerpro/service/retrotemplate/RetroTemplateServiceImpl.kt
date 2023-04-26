package com.scrumpokerpro.service.retrotemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.mapper.toEntity
import com.scrumpokerpro.mapper.toTemplateEntity
import com.scrumpokerpro.repository.RetroTemplateRepository
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class RetroTemplateServiceImpl(
    val retroTemplateRepository: RetroTemplateRepository,
    val objectMapper: ObjectMapper
) : RetroTemplateService {

    @Transactional
    override suspend fun createTemplate(userId: UUID, dto: RetroTemplateDto): RetroTemplateDto {
        val retroTemplate = retroTemplateRepository.findByUserId(userId)?.copy(
            columns = Json.of(objectMapper.writeValueAsString(dto.columns.map { it.toTemplateEntity() })),
            modified = LocalDateTime.now()
        ) ?: dto.toEntity(userId)
        return retroTemplateRepository.save(retroTemplate).toDto()
    }

    override suspend fun getTemplate(userId: UUID): RetroTemplateDto? {
        return retroTemplateRepository.findByUserId(userId)?.toDto()
    }
}
