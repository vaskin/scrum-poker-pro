package com.scrumpokerpro.service.email

import com.scrumpokerpro.config.ScrumPokerPro
import com.scrumpokerpro.dto.email.InvitationMailDto
import com.scrumpokerpro.dto.email.ShareResultsDto
import com.scrumpokerpro.entity.meeting.MeetingType
import com.scrumpokerpro.service.groupinvite.GroupInviteService
import com.scrumpokerpro.service.meeting.MeetingService
import freemarker.template.Configuration
import freemarker.template.Template
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString
import java.io.StringReader
import java.util.UUID
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
class EmailServiceImpl(
    val emailSender: JavaMailSender,
    val groupInviteService: GroupInviteService,
    val meetingService: MeetingService,
    val resourceLoader: ResourceLoader,
    val scrumPokerPro: ScrumPokerPro,
    val freemarkerConfig: Configuration,
) : EmailService {

    override suspend fun sendInvitation(userId: UUID, invitationMailDto: InvitationMailDto) {
        val meeting = meetingService.getMeeting(id = invitationMailDto.meetingId, userId = userId).also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
        }
        val template = resourceLoader.getResource("classpath:templates/invitation.html").file.readText(charset = Charsets.UTF_8)
        val emails = invitationMailDto.groupInviteId?.let {
            groupInviteService.getGroupInviteMembers(userId, invitationMailDto.groupInviteId).map { it.email }
        } ?: listOf(invitationMailDto.email!!)

        return withContext(Dispatchers.IO) {
            val invitation = Template("template", StringReader(template), freemarkerConfig).let {
                val context = mutableMapOf<String, Any>()
                val actionType = if (meeting.type == MeetingType.PLANNING) "meeting" else "retro-meeting"
                val action = "${scrumPokerPro.email.baseUrl}/$actionType/${meeting.id}"
                context["meeting"] = meeting.name
                context["action"] = action
                context["type"] = if (meeting.type == MeetingType.PLANNING) "sprint planning" else "retrospective"
                processTemplateIntoString(it, context)
            }

            val mimeMessage: MimeMessage = emailSender.createMimeMessage()
            MimeMessageHelper(mimeMessage, true).apply {
                setFrom(InternetAddress(scrumPokerPro.email.from, scrumPokerPro.email.displayName))
                setTo(emails.toTypedArray())
                setSubject("You've been invited to the ${meeting.name}")
                setText(invitation, true)
            }
            emailSender.send(mimeMessage)
        }
    }

    override suspend fun shareRetroResults(userId: UUID, shareResultsDto: ShareResultsDto) {
        val meeting = meetingService.getMeeting(id = shareResultsDto.meetingId, userId = userId).also {
            if (it.userId != userId) throw AccessDeniedException("Forbidden")
        }
        val template = resourceLoader.getResource("classpath:templates/results.html").file.readText(charset = Charsets.UTF_8)
        val emails = shareResultsDto.groupInviteId?.let {
            groupInviteService.getGroupInviteMembers(userId, shareResultsDto.groupInviteId).map { it.email }
        } ?: listOf(shareResultsDto.email!!)

        return withContext(Dispatchers.IO) {
            val invitation = Template("template", StringReader(template), freemarkerConfig).let {
                val context = mutableMapOf<String, Any>()
                context["meeting"] = meeting
                processTemplateIntoString(it, context)
            }

            val mimeMessage: MimeMessage = emailSender.createMimeMessage()
            MimeMessageHelper(mimeMessage, true).apply {
                setFrom(InternetAddress(scrumPokerPro.email.from, scrumPokerPro.email.displayName))
                setTo(emails.toTypedArray())
                setSubject("[Retro] ${meeting.name}")
                setText(invitation, true)
            }
            emailSender.send(mimeMessage)
        }
    }
}
