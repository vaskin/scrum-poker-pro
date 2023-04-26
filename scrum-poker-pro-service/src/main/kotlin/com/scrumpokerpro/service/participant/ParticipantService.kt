package com.scrumpokerpro.service.participant

import com.scrumpokerpro.dto.profile.ParticipantDto
import com.scrumpokerpro.entity.participant.Participant
import java.util.UUID

interface ParticipantService {

    suspend fun getParticipantsDto(meetingId: UUID): List<ParticipantDto>

    suspend fun createParticipant(meetingId: UUID, name: String, userId: UUID)

    suspend fun deleteParticipant(meetingId: UUID, userId: UUID)

    suspend fun getParticipants(meetingId: UUID): List<Participant>
}
