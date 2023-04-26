package com.scrumpokerpro.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.scrumpokerpro.dto.meeting.CreateMeetingDto
import com.scrumpokerpro.dto.meeting.MeetingDto
import com.scrumpokerpro.dto.meeting.RetroColumnDto
import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import com.scrumpokerpro.dto.sticker.StickerDto
import com.scrumpokerpro.entity.meeting.Meeting
import com.scrumpokerpro.entity.meeting.retro.RetroColumn
import com.scrumpokerpro.entity.meeting.retro.RetroColumnTemplate
import com.scrumpokerpro.entity.meeting.retro.RetroTemplate
import io.r2dbc.postgresql.codec.Json
import java.time.LocalDateTime
import java.util.UUID

fun CreateMeetingDto.toEntity(userId: UUID, now: LocalDateTime = LocalDateTime.now()) = Meeting(
    name = name,
    type = type,
    votingSystem = votingSystem,
    userId = userId,
    created = now,
    modified = now
)

fun RetroColumnDto.toEntity(meetingId: UUID, position: Int, now: LocalDateTime = LocalDateTime.now()) = RetroColumn(
    name = name,
    color = color,
    position = position,
    meetingId = meetingId,
    created = now,
    modified = now
)

fun RetroColumn.toDto(stickers: List<StickerDto>) = RetroColumnDto(
    id = id,
    name = name,
    color = color,
    stickers = stickers
)

fun Meeting.toDto(columns: List<RetroColumnDto> = listOf()) = MeetingDto(
    id = id!!,
    name = name,
    type = type,
    votingSystem = votingSystem,
    userId = userId,
    currentIssueId = currentIssueId,
    columns = columns,
    created = created,
    modified = created
)

fun RetroColumnDto.toTemplateEntity() = RetroColumnTemplate(
    name = name,
    color = color
)

fun RetroTemplateDto.toEntity(userId: UUID, now: LocalDateTime = LocalDateTime.now()) = RetroTemplate(
    userId = userId,
    columns = Json.of(jacksonObjectMapper().writeValueAsString(columns.map { it.toTemplateEntity() })),
    created = now,
    modified = now
)

fun RetroTemplate.toDto() = RetroTemplateDto(
    columns = jacksonObjectMapper().readValue(columns.asString())
)
