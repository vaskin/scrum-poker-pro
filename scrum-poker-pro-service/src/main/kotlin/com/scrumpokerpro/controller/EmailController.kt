package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.email.InvitationMailDto
import com.scrumpokerpro.dto.email.ShareResultsDto
import com.scrumpokerpro.service.email.EmailService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.userId
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/emails")
class EmailController(
    val emailService: EmailService,
) {

    val log by logger()

    @PostMapping("/invitations")
    suspend fun sendInvitation(
        @RequestBody invitationMailDto: InvitationMailDto,
        @AuthenticationPrincipal principal: Jwt,
    ) {
        emailService.sendInvitation(principal.userId(), invitationMailDto)
        log.info("email sent: invitationMailDto = {}, userId = {}", invitationMailDto, principal.userId())
    }

    @PostMapping("/retro-results")
    suspend fun shareRetroResults(
        @RequestBody shareResultsDto: ShareResultsDto,
        @AuthenticationPrincipal principal: Jwt,
    ) {
        emailService.shareRetroResults(principal.userId(), shareResultsDto)
        log.info("share retro results: shareResultsDto = {}, userId = {}", shareResultsDto, principal.userId())
    }
}
