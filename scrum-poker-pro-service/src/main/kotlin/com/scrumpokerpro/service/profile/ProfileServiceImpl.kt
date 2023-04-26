package com.scrumpokerpro.service.profile

import com.scrumpokerpro.dto.profile.ProfileDto
import com.scrumpokerpro.entity.profile.Profile
import com.scrumpokerpro.repository.ProfileRepository
import com.scrumpokerpro.service.file.FileService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

@Service
class ProfileServiceImpl(
    val profileRepository: ProfileRepository,
    val fileService: FileService,
    @Value("\${scrum-poker-pro.s3.avatar-bucket}") val avatarBucket: String
) : ProfileService {

    @Transactional
    override suspend fun updateProfile(name: String, avatar: FilePart?, userId: UUID): ProfileDto {
        val now = LocalDateTime.now()
        val avatarKey = avatar?.let {
            fileService.upload(file = avatar, bucket = avatarBucket, key = userId.toString())
            userId
        }
        val profile = profileRepository.findByUserId(userId)?.apply {
            profileRepository.save(
                this.copy(
                    name = name,
                    avatarKey = avatarKey ?: this.avatarKey,
                    modified = now
                )
            )
        } ?: profileRepository.save(
            Profile(
                userId = userId,
                name = name,
                avatarKey = avatarKey,
                created = now,
                modified = now
            )
        )
        return getProfile(name = profile.name, userId = userId)
    }

    @Transactional
    override suspend fun deleteAvatar(name: String, userId: UUID): ProfileDto {
        val profile = profileRepository.findByUserId(userId)?.apply {
            profileRepository.save(this.copy(avatarKey = null))
        }
        return getProfile(name = profile?.name ?: name, userId = userId)
    }

    override suspend fun getName(name: String, userId: UUID) = profileRepository.findByUserId(userId)?.name ?: name

    override suspend fun getProfile(name: String, userId: UUID): ProfileDto {
        return profileRepository.findByUserId(userId)?.let {
            val file = if (it.avatarKey != null) {
                fileService.download(bucket = avatarBucket, key = userId.toString())
            } else null
            ProfileDto(
                userId = it.userId,
                name = it.name,
                avatar = if (file != null) Base64.getEncoder().encodeToString(file.asByteArray()) else null,
                contentType = file?.response()?.contentType(),
                fileName = file?.response()?.contentDisposition()
            )
        } ?: ProfileDto(userId = userId, name = name)
    }

    override suspend fun getProfile(userId: UUID): Profile? {
        return profileRepository.findByUserId(userId)
    }

    override suspend fun getAvatar(userId: UUID): ResponseBytes<GetObjectResponse> {
        return fileService.download(bucket = avatarBucket, key = userId.toString())
    }
}
