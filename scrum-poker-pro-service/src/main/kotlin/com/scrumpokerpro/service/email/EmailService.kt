package com.scrumpokerpro.service.email

import com.scrumpokerpro.dto.email.InvitationMailDto
import com.scrumpokerpro.dto.email.ShareResultsDto
import java.util.UUID

interface EmailService {

    suspend fun sendInvitation(userId: UUID, invitationMailDto: InvitationMailDto)

    suspend fun shareRetroResults(userId: UUID, shareResultsDto: ShareResultsDto)
}
