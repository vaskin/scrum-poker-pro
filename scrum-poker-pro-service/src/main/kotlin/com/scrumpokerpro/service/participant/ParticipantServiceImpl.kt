package com.scrumpokerpro.service.participant

import com.scrumpokerpro.dto.profile.ParticipantDto
import com.scrumpokerpro.entity.participant.Participant
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.repository.ParticipantRepository
import com.scrumpokerpro.service.profile.ProfileService
import com.scrumpokerpro.utils.logger
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ParticipantServiceImpl(
    val participantRepository: ParticipantRepository,
    val profileService: ProfileService,
) : ParticipantService {

    val log by logger()

    override suspend fun getParticipantsDto(meetingId: UUID): List<ParticipantDto> {
        return participantRepository.findByMeetingId(meetingId).map {
            val profile = profileService.getProfile(name = it.name, userId = it.userId)
            it.toDto(avatar = profile.avatar, contentType = profile.contentType, fileName = profile.fileName)
        }
    }

    @Transactional
    override suspend fun deleteParticipant(meetingId: UUID, userId: UUID) {
        log.info("delete participant [meetingId = {}, userId = {}]", meetingId, userId)
        participantRepository.findByMeetingId(meetingId).find {
            it.userId == userId
        }?.also {
            participantRepository.delete(it)
            log.info("participant deleted [meetingId = {}, userId = {}]", meetingId, userId)
        }
    }

    override suspend fun getParticipants(meetingId: UUID): List<Participant> {
        return participantRepository.findByMeetingId(meetingId)
    }

    @Transactional
    override suspend fun createParticipant(meetingId: UUID, name: String, userId: UUID) {
        val now = LocalDateTime.now()
        if (!participantRepository.findByMeetingId(meetingId).any { it.userId == userId }) {
            Participant(
                meetingId = meetingId,
                name = profileService.getName(name, userId),
                userId = userId,
                lastActivity = now,
                created = now,
                modified = now
            ).also {
                runCatching {
                    participantRepository.save(it)
                }.onFailure {
                    log.error("create participant failed", it)
                }
            }
        }
    }
}
