package com.scrumpokerpro.service.profile

import com.scrumpokerpro.dto.profile.ProfileDto
import com.scrumpokerpro.entity.profile.Profile
import org.springframework.http.codec.multipart.FilePart
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import java.util.UUID

interface ProfileService {

    suspend fun getName(name: String, userId: UUID): String

    suspend fun getAvatar(userId: UUID): ResponseBytes<GetObjectResponse>

    suspend fun getProfile(userId: UUID): Profile?

    suspend fun getProfile(name: String, userId: UUID): ProfileDto

    suspend fun updateProfile(name: String, avatar: FilePart?, userId: UUID): ProfileDto

    suspend fun deleteAvatar(name: String, userId: UUID): ProfileDto
}
