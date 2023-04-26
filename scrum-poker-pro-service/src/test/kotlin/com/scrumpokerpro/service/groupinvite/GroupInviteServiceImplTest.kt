package com.scrumpokerpro.service.groupinvite

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.groupinvite.CreateGroupInviteDto
import com.scrumpokerpro.dto.groupinvite.UpdateGroupInviteDto
import com.scrumpokerpro.entity.groupinvite.GroupInvite
import com.scrumpokerpro.entity.groupinvite.GroupInviteMember
import com.scrumpokerpro.repository.GroupInviteMemberRepository
import com.scrumpokerpro.repository.GroupInviteRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class GroupInviteServiceImplTest(
    @Autowired
    val groupInviteService: GroupInviteService,
    @Autowired
    val groupInviteRepository: GroupInviteRepository,
    @Autowired
    val groupInviteMemberRepository: GroupInviteMemberRepository
) {

    @Test
    fun `should create group`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val createGroupInviteDto = CreateGroupInviteDto(
                name = "my group",
                emails = listOf("test@scrumpokerpro.com")
            )

            val group = groupInviteService.createGroupInvite(userId, createGroupInviteDto)
            val createdGroup = groupInviteRepository.findById(group.id)
            val createdGroupMembers = groupInviteMemberRepository.findByGroupInviteId(group.id)
            assertEquals(createGroupInviteDto.name, createdGroup?.name)
            assertEquals(createGroupInviteDto.emails[0], createdGroupMembers[0].email)
        }
    }

    @Test
    fun `should update group`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val group = groupInviteRepository.save(
                GroupInvite(
                    name = "my group",
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            groupInviteMemberRepository.save(
                GroupInviteMember(
                    email = "test@scrumpokerpro.com",
                    groupInviteId = group.id!!,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            val updateGroupInviteDto = UpdateGroupInviteDto(
                name = "delta team",
                emails = listOf("kv@scrumpokerpro.com")
            )

            groupInviteService.updateGroupInvite(userId, group.id!!, updateGroupInviteDto)
            val createdGroup = groupInviteRepository.findById(group.id!!)
            val createdGroupMembers = groupInviteMemberRepository.findByGroupInviteId(group.id!!)
            assertEquals(updateGroupInviteDto.name, createdGroup?.name)
            assertEquals(updateGroupInviteDto.emails[0], createdGroupMembers[0].email)
        }
    }

    @Test
    fun `should delete group`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val group = groupInviteRepository.save(
                GroupInvite(
                    name = "my group",
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            groupInviteMemberRepository.save(
                GroupInviteMember(
                    email = "test@scrumpokerpro.com",
                    groupInviteId = group.id!!,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )

            groupInviteService.deleteGroupInvite(userId, group.id!!)
            val createdGroup = groupInviteRepository.findById(group.id!!)
            val createdGroupMembers = groupInviteMemberRepository.findByGroupInviteId(group.id!!)
            assertNull(createdGroup)
            assertTrue(createdGroupMembers.isEmpty())
        }
    }

    @Test
    fun `should get groups`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val group = groupInviteRepository.save(
                GroupInvite(
                    name = "my group",
                    userId = userId,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )
            groupInviteMemberRepository.save(
                GroupInviteMember(
                    email = "test@scrumpokerpro.com",
                    groupInviteId = group.id!!,
                    created = LocalDateTime.now(),
                    modified = LocalDateTime.now()
                )
            )

            val groups = groupInviteService.getGroupInvites(userId)
            assertEquals(group.name, groups[0].name)
            assertEquals("test@scrumpokerpro.com", groups[0].emails[0])
        }
    }
}
