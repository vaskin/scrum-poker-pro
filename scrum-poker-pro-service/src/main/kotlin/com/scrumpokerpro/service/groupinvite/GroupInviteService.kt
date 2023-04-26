package com.scrumpokerpro.service.groupinvite

import com.scrumpokerpro.dto.groupinvite.CreateGroupInviteDto
import com.scrumpokerpro.dto.groupinvite.GroupInviteDto
import com.scrumpokerpro.dto.groupinvite.UpdateGroupInviteDto
import com.scrumpokerpro.entity.groupinvite.GroupInviteMember
import java.util.UUID

interface GroupInviteService {

    suspend fun createGroupInvite(userId: UUID, createGroupInviteDto: CreateGroupInviteDto): GroupInviteDto

    suspend fun updateGroupInvite(userId: UUID, groupInviteId: UUID, updateGroupInviteDto: UpdateGroupInviteDto): GroupInviteDto

    suspend fun getGroupInvites(userId: UUID): List<GroupInviteDto>

    suspend fun deleteGroupInvite(userId: UUID, groupInviteId: UUID)

    suspend fun getGroupInviteMembers(userId: UUID, groupInviteId: UUID): List<GroupInviteMember>
}
