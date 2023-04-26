package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.meeting.retro.RetroColumn
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface RetroColumnRepository : CoroutineCrudRepository<RetroColumn, UUID> {

    suspend fun findByMeetingId(meetingId: UUID): List<RetroColumn>
}
