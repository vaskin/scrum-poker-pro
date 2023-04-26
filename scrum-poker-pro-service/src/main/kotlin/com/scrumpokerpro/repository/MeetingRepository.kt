package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.meeting.Meeting
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface MeetingRepository : CoroutineCrudRepository<Meeting, UUID> {

    suspend fun findByUserId(userId: UUID): List<Meeting>
}
