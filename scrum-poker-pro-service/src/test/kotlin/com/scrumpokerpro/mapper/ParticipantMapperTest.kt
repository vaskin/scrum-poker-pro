package com.scrumpokerpro.mapper

import com.scrumpokerpro.entity.participant.Participant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class ParticipantMapperTest {

    @Test
    fun `should convert Participant to ParticipantDto`() {
        val participant = Participant(
            id = UUID.randomUUID(),
            name = "Olga",
            meetingId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            lastActivity = LocalDateTime.now(),
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )

        val participantDto = participant.toDto(avatar = "base64")
        assertEquals(participant.name, participantDto.name)
        assertEquals("base64", participantDto.avatar)
        assertEquals(participant.lastActivity, participantDto.lastActivity)
        assertEquals(participant.created, participantDto.created)
        assertEquals(participant.modified, participantDto.modified)
    }
}
