package com.scrumpokerpro.service.meeting

import com.scrumpokerpro.dto.meeting.CreateMeetingDto
import com.scrumpokerpro.dto.meeting.MeetingDto
import com.scrumpokerpro.dto.meeting.UpdateMeetingDto
import java.util.UUID

interface MeetingService {

    suspend fun getMeetings(userId: UUID): List<MeetingDto>

    suspend fun getMeeting(id: UUID, userId: UUID): MeetingDto

    suspend fun createMeeting(name: String, userId: UUID, dto: CreateMeetingDto): MeetingDto

    suspend fun updateMeeting(id: UUID, userId: UUID, dto: UpdateMeetingDto): MeetingDto

    suspend fun deleteMeeting(id: UUID, userId: UUID)
}
