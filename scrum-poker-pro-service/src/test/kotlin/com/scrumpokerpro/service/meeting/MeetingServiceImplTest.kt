package com.scrumpokerpro.service.meeting

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.meeting.CreateMeetingDto
import com.scrumpokerpro.dto.meeting.RetroColumnDto
import com.scrumpokerpro.dto.meeting.UpdateMeetingDto
import com.scrumpokerpro.entity.meeting.Meeting
import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.entity.meeting.MeetingVotingSystem
import com.scrumpokerpro.repository.MeetingRepository
import com.scrumpokerpro.repository.RetroColumnRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class MeetingServiceImplTest(
    @Autowired
    val meetingService: MeetingService,
    @Autowired
    val meetingRepository: MeetingRepository,
    @Autowired
    val retroColumnRepository: RetroColumnRepository
) {

    @Test
    fun `should create planning meeting`() {
        runBlocking {
            val createMeetingDto = CreateMeetingDto(
                name = "name",
                type = MeetingType.PLANNING,
                votingSystem = MeetingVotingSystem.FIBONACCI
            )

            val meetingId = meetingService.createMeeting("Alex", UUID.randomUUID(), createMeetingDto).id
            val createdMeeting = meetingRepository.findById(meetingId)
            assertEquals(createMeetingDto.name, createdMeeting?.name)
            assertEquals(createMeetingDto.type, createdMeeting?.type)
            assertEquals(createMeetingDto.votingSystem, createdMeeting?.votingSystem)
            assertNotNull(createdMeeting?.userId)
            assertNotNull(createdMeeting?.created)
            assertNotNull(createdMeeting?.modified)
        }
    }

    @Test
    fun `should update current issue`() {
        runBlocking {
            val meeting = Meeting(
                name = "name",
                type = MeetingType.PLANNING,
                votingSystem = MeetingVotingSystem.FIBONACCI,
                userId = UUID.randomUUID(),
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            )
            val meetingId = meetingRepository.save(meeting).id!!
            val updateMeetingDto = UpdateMeetingDto(
                currentIssueId = UUID.randomUUID()
            )

            meetingService.updateMeeting(meetingId, meeting.userId, updateMeetingDto)
            val updatedMeeting = meetingRepository.findById(meetingId)
            assertEquals(updateMeetingDto.currentIssueId, updatedMeeting?.currentIssueId)
        }
    }

    @Test
    fun `should delete meeting`() {
        runBlocking {
            val meeting = Meeting(
                name = "name",
                type = MeetingType.PLANNING,
                votingSystem = MeetingVotingSystem.FIBONACCI,
                userId = UUID.randomUUID(),
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            )
            val meetingId = meetingRepository.save(meeting).id!!

            meetingService.deleteMeeting(meetingId, meeting.userId)
            assertNull(meetingRepository.findById(meetingId))
        }
    }

    @Test
    fun `should create retro meeting`() {
        runBlocking {
            val createMeetingDto = CreateMeetingDto(
                name = "name",
                type = MeetingType.RETRO,
                columns = listOf(RetroColumnDto(name = "What went wrong?", color = "#FF0000"))
            )

            val meetingId = meetingService.createMeeting("Alex", UUID.randomUUID(), createMeetingDto).id
            val createdMeeting = meetingRepository.findById(meetingId)
            val columns = retroColumnRepository.findByMeetingId(meetingId)
            assertEquals(createMeetingDto.name, createdMeeting?.name)
            assertEquals(createMeetingDto.type, createdMeeting?.type)
            assertEquals(createMeetingDto.votingSystem, createdMeeting?.votingSystem)
            assertNotNull(createdMeeting?.userId)
            assertNotNull(createdMeeting?.created)
            assertNotNull(createdMeeting?.modified)
            assertTrue(columns.size == 1)
            assertEquals(createMeetingDto.columns[0].color, columns[0].color)
            assertEquals(createMeetingDto.columns[0].name, columns[0].name)
        }
    }
}
