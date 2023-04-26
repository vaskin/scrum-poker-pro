package com.scrumpokerpro.service.meeting

import com.scrumpokerpro.dto.meeting.CreateMeetingDto
import com.scrumpokerpro.dto.meeting.MeetingDto
import com.scrumpokerpro.dto.meeting.UpdateMeetingDto
import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.mapper.toEntity
import com.scrumpokerpro.repository.MeetingRepository
import com.scrumpokerpro.repository.RetroColumnRepository
import com.scrumpokerpro.service.participant.ParticipantService
import com.scrumpokerpro.service.sticker.StickerService
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class MeetingServiceImpl(
    val meetingRepository: MeetingRepository,
    val retroColumnRepository: RetroColumnRepository,
    val participantService: ParticipantService,
    val stickerService: StickerService,
) : MeetingService {

    override suspend fun getMeeting(id: UUID, userId: UUID): MeetingDto {
        return meetingRepository.findById(id)?.toDto()?.also {
            val columns = if (it.type == MeetingType.RETRO) {
                retroColumnRepository.findByMeetingId(id).sortedBy { retroColumn ->
                    retroColumn.position
                }.map { retroColumn ->
                    retroColumn.toDto(stickers = stickerService.getStickers(userId, retroColumn.id!!))
                }
            } else listOf()
            it.columns = columns
        } ?: throw EntityNotFoundException()
    }

    override suspend fun getMeetings(userId: UUID): List<MeetingDto> {
        return meetingRepository.findByUserId(userId).sortedByDescending { it.created }.map { it.toDto() }
    }

    @Transactional
    override suspend fun createMeeting(name: String, userId: UUID, dto: CreateMeetingDto): MeetingDto {
        return meetingRepository.save(dto.toEntity(userId = userId)).also { meeting ->
            var position = 0
            dto.columns.map { it.toEntity(meetingId = meeting.id!!, position = ++position) }.forEach {
                retroColumnRepository.save(it)
            }
            participantService.createParticipant(meetingId = meeting.id!!, name = name, userId = userId)
        }.toDto(dto.columns)
    }

    @Transactional
    override suspend fun updateMeeting(id: UUID, userId: UUID, dto: UpdateMeetingDto): MeetingDto {
        return meetingRepository.findById(id)?.also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
            meetingRepository.save(
                it.copy(currentIssueId = dto.currentIssueId, modified = LocalDateTime.now())
            )
        }?.toDto() ?: throw EntityNotFoundException()
    }

    @Transactional
    override suspend fun deleteMeeting(id: UUID, userId: UUID) {
        meetingRepository.findById(id)?.also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
            meetingRepository.deleteById(id)
        }
    }
}
