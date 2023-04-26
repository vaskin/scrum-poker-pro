package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.profile.Profile
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface ProfileRepository : CoroutineCrudRepository<Profile, UUID> {

    suspend fun findByUserId(userId: UUID): Profile?
}
