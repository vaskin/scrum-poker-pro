package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.sticker.CreateStickerDto
import com.scrumpokerpro.dto.sticker.StickerDto
import com.scrumpokerpro.dto.sticker.StickerLikeDto
import com.scrumpokerpro.dto.sticker.UpdateStickerDto
import com.scrumpokerpro.mapper.toDto
import com.scrumpokerpro.service.sticker.StickerService
import com.scrumpokerpro.service.sticker.like.LikeService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.name
import com.scrumpokerpro.utils.userId
import org.springframework.http.HttpEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/stickers")
class StickerController(
    val stickerService: StickerService,
    val likeService: LikeService,
) {

    val log by logger()

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun createSticker(
        @RequestBody dto: CreateStickerDto,
        @AuthenticationPrincipal principal: Jwt,
    ): StickerDto {
        log.info("create sticker [userId = {}, dto = {}]", principal.userId(), dto)
        return stickerService.createSticker(principal.userId(), principal.name(), dto)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateSticker(
        @PathVariable id: UUID,
        @RequestBody dto: UpdateStickerDto,
        @AuthenticationPrincipal principal: Jwt,
    ): StickerDto {
        log.info("update sticker [id = {}, userId = {}, dto = {}]", id, principal.userId(), dto)
        return stickerService.updateSticker(id, principal.userId(), dto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteSticker(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: Jwt,
    ) {
        log.info("delete sticker [id = {}, userId = {}]", id, principal.userId())
        return stickerService.deleteSticker(id, principal.userId())
    }

    @GetMapping("/{id}/avatars")
    @PreAuthorize("hasRole('USER')")
    suspend fun getAvatar(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: Jwt,
    ): HttpEntity<ByteArray> {
        return stickerService.getAvatar(id)
    }

    @PostMapping("/{stickerId}/likes")
    @PreAuthorize("hasRole('USER')")
    suspend fun createLike(
        @PathVariable stickerId: UUID,
        @AuthenticationPrincipal principal: Jwt,
    ): StickerLikeDto {
        log.info("create like [userId = {}, stickerId = {}]", principal.userId(), stickerId)
        return likeService.createLike(stickerId, principal.userId()).toDto()
    }

    @DeleteMapping("/{stickerId}/likes")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteLike(
        @PathVariable stickerId: UUID,
        @AuthenticationPrincipal principal: Jwt,
    ) {
        log.info("delete like [userId = {}, stickerId = {}]", principal.userId(), stickerId)
        return likeService.deleteLike(stickerId, principal.userId())
    }
}
