package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.groupinvite.GroupInviteMember
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface GroupInviteMemberRepository : CoroutineCrudRepository<GroupInviteMember, UUID> {

    suspend fun findByGroupInviteId(groupInviteId: UUID): List<GroupInviteMember>
}
