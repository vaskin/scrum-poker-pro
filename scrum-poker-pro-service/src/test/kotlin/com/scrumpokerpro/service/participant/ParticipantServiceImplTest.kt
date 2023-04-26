package com.scrumpokerpro.service.participant

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.repository.ParticipantRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class ParticipantServiceImplTest(
    @Autowired
    val participantService: ParticipantService,
    @Autowired
    val participantRepository: ParticipantRepository
) {

    @Test
    fun `should create participant`() {
        runBlocking {
            participantRepository.deleteAll()
            val meetingId = UUID.randomUUID()
            val userId = UUID.randomUUID()

            participantService.createParticipant(meetingId = meetingId, name = "Josh", userId = userId)
            val createdParticipant = participantRepository.findAll().first()
            assertEquals("Josh", createdParticipant.name)
            assertEquals(meetingId, createdParticipant.meetingId)
            assertEquals(userId, createdParticipant.userId)
            assertNotNull(createdParticipant.lastActivity)
            assertNotNull(createdParticipant.created)
            assertNotNull(createdParticipant.modified)
        }
    }

    @Test
    fun `should get participants`() {
        runBlocking {
            participantRepository.deleteAll()
            val meetingId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            participantService.createParticipant(meetingId = meetingId, name = "Josh", userId = userId)

            val participant = participantService.getParticipantsDto(meetingId).first()
            assertEquals("Josh", participant.name)
            assertNull(participant.avatar)
            assertNotNull(participant.lastActivity)
            assertNotNull(participant.created)
            assertNotNull(participant.modified)
        }
    }
}
