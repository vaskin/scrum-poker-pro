package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.profile.ProfileDto
import com.scrumpokerpro.service.profile.ProfileService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.name
import com.scrumpokerpro.utils.userId
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profiles")
class ProfileController(
    val profileService: ProfileService
) {

    val log by logger()

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    suspend fun getCurrentProfile(@AuthenticationPrincipal principal: Jwt): ProfileDto {
        return profileService.getProfile(name = principal.name(), userId = principal.userId())
    }

    @PutMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    suspend fun updateProfile(
        @RequestPart(name = "name") name: String,
        @RequestPart(name = "avatar", required = false) avatar: FilePart,
        @AuthenticationPrincipal principal: Jwt
    ): ProfileDto {
        log.info("update profile [userId = {}, name = {}]", principal.userId(), name)
        return profileService.updateProfile(name = name, avatar = avatar, userId = principal.userId())
    }

    @DeleteMapping("/avatars")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteAvatar(@AuthenticationPrincipal principal: Jwt): ProfileDto {
        return profileService.deleteAvatar(name = principal.getClaimAsString("name"), userId = principal.userId())
    }
}
