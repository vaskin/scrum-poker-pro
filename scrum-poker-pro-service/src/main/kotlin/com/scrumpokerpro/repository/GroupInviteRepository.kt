package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.groupinvite.GroupInvite
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface GroupInviteRepository : CoroutineCrudRepository<GroupInvite, UUID> {

    suspend fun findByUserId(userId: UUID): List<GroupInvite>
}
