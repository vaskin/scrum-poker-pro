package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.participant.Participant
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface ParticipantRepository : CoroutineCrudRepository<Participant, UUID> {

    suspend fun findByMeetingId(meetingId: UUID): List<Participant>
}
