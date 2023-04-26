package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import com.scrumpokerpro.service.retrotemplate.RetroTemplateService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.userId
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/templates")
class TemplateController(
    val retroTemplateService: RetroTemplateService,
) {

    val log by logger()

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun getTemplate(
        @AuthenticationPrincipal principal: Jwt,
    ): RetroTemplateDto? {
        return retroTemplateService.getTemplate(userId = principal.userId())
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun createTemplate(
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody dto: RetroTemplateDto
    ): RetroTemplateDto {
        log.info("create retro template [userId = {}, dto = {}]", principal.userId(), dto)
        return retroTemplateService.createTemplate(userId = principal.userId(), dto = dto)
    }
}
