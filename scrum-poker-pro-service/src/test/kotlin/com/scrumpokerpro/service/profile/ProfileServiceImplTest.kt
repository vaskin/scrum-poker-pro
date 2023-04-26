package com.scrumpokerpro.service.profile

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.entity.profile.Profile
import com.scrumpokerpro.repository.ProfileRepository
import com.scrumpokerpro.service.file.FileService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class ProfileServiceImplTest(
    @Autowired
    val profileService: ProfileService,
    @Autowired
    val profileRepository: ProfileRepository
) {

    @MockBean
    lateinit var fileService: FileService

    @Test
    fun `should create new profile`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val filePart = Mockito.mock(FilePart::class.java)
            profileService.updateProfile(
                name = "Peter",
                userId = userId,
                avatar = filePart
            )

            val createdProfile = profileRepository.findByUserId(userId)
            assertEquals("Peter", createdProfile?.name)
            assertNotNull(createdProfile?.avatarKey)
        }
    }

    @Test
    fun `should update profile`() {
        runBlocking {
            val filePart = Mockito.mock(FilePart::class.java)
            val userId = UUID.randomUUID()
            val profile = profileRepository.save(
                Profile(
                    name = "Peter",
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            profileService.updateProfile(
                name = "Alex",
                userId = profile.userId,
                avatar = filePart
            )

            val updatedProfile = profileRepository.findByUserId(userId)
            assertEquals("Alex", updatedProfile?.name)
            assertNotNull(updatedProfile?.avatarKey)
        }
    }
}
