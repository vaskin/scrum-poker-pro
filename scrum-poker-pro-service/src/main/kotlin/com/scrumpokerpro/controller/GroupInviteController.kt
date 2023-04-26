package com.scrumpokerpro.controller

import com.scrumpokerpro.dto.groupinvite.CreateGroupInviteDto
import com.scrumpokerpro.dto.groupinvite.GroupInviteDto
import com.scrumpokerpro.dto.groupinvite.UpdateGroupInviteDto
import com.scrumpokerpro.service.groupinvite.GroupInviteService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.utils.userId
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
@RequestMapping("/group-invites")
class GroupInviteController(
    val groupInviteService: GroupInviteService
) {

    val log by logger()

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun createGroupInvite(
        @RequestBody createGroupInviteDto: CreateGroupInviteDto,
        @AuthenticationPrincipal principal: Jwt
    ): GroupInviteDto {
        log.info("create group invite [userId = {}, createIssueDto = {}]", principal.userId(), createGroupInviteDto)
        return groupInviteService.createGroupInvite(principal.userId(), createGroupInviteDto)
    }

    @PutMapping("/{groupInviteId}")
    @PreAuthorize("hasRole('USER')")
    suspend fun updateGroupInvite(
        @PathVariable groupInviteId: UUID,
        @RequestBody updateGroupInviteDto: UpdateGroupInviteDto,
        @AuthenticationPrincipal principal: Jwt
    ): GroupInviteDto {
        log.info(
            "update group invite [userId = {}, groupInviteId = {}, createIssueDto = {}]", principal.userId(), groupInviteId,
            updateGroupInviteDto
        )
        return groupInviteService.updateGroupInvite(
            userId = principal.userId(),
            groupInviteId = groupInviteId,
            updateGroupInviteDto = updateGroupInviteDto
        )
    }

    @DeleteMapping("/{groupInviteId}")
    @PreAuthorize("hasRole('USER')")
    suspend fun deleteGroupInvite(
        @PathVariable groupInviteId: UUID,
        @AuthenticationPrincipal principal: Jwt
    ) {
        log.info("delete group invite [userId = {}, groupInviteId = {}]", principal.userId(), groupInviteId)
        groupInviteService.deleteGroupInvite(principal.userId(), groupInviteId)
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    suspend fun getGroupInvites(
        @AuthenticationPrincipal principal: Jwt
    ): List<GroupInviteDto> {
        log.info("get group invites [userId = {}]", principal.userId())
        return groupInviteService.getGroupInvites(principal.userId())
    }
}
