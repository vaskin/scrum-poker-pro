package com.scrumpokerpro.service.email

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.email.InvitationMailDto
import com.scrumpokerpro.entity.groupinvite.GroupInvite
import com.scrumpokerpro.entity.groupinvite.GroupInviteMember
import com.scrumpokerpro.entity.meeting.Meeting
import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.entity.meeting.MeetingVotingSystem
import com.scrumpokerpro.repository.GroupInviteMemberRepository
import com.scrumpokerpro.repository.GroupInviteRepository
import com.scrumpokerpro.repository.MeetingRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID
import javax.mail.internet.MimeMessage

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class EmailServiceImplTest(
    @Autowired
    val emailService: EmailService,
    @Autowired
    val groupInviteRepository: GroupInviteRepository,
    @Autowired
    val groupInviteMemberRepository: GroupInviteMemberRepository,
    @Autowired
    val meetingRepository: MeetingRepository,

) {
    @SpyBean
    lateinit var mailSender: JavaMailSender

    @Test
    fun `should send invitation`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val group = groupInviteRepository.save(
                GroupInvite(
                    name = "my group",
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            groupInviteMemberRepository.save(
                GroupInviteMember(
                    email = "test@scrumpokerpro.com",
                    groupInviteId = group.id!!,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            val meeting = Meeting(
                name = "name",
                type = MeetingType.PLANNING,
                votingSystem = MeetingVotingSystem.FIBONACCI,
                userId = userId,
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                meetingRepository.save(it)
            }
            doNothing().`when`(mailSender).send(any(MimeMessage::class.java))

            emailService.sendInvitation(
                userId,
                InvitationMailDto(
                    groupInviteId = group.id,
                    meetingId = meeting.id!!
                )
            )
            verify(mailSender).send(any(MimeMessage::class.java))
        }
    }
}
