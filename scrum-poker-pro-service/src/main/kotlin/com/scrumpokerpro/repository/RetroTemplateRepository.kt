package com.scrumpokerpro.repository

import com.scrumpokerpro.entity.meeting.retro.RetroTemplate
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface RetroTemplateRepository : CoroutineCrudRepository<RetroTemplate, UUID> {

    suspend fun findByUserId(userId: UUID): RetroTemplate?
}
