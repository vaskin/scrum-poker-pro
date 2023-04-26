package com.scrumpokerpro.service.groupinvite

import com.scrumpokerpro.dto.groupinvite.CreateGroupInviteDto
import com.scrumpokerpro.dto.groupinvite.GroupInviteDto
import com.scrumpokerpro.dto.groupinvite.UpdateGroupInviteDto
import com.scrumpokerpro.entity.groupinvite.GroupInvite
import com.scrumpokerpro.entity.groupinvite.GroupInviteMember
import com.scrumpokerpro.exception.EntityNotFoundException
import com.scrumpokerpro.repository.GroupInviteMemberRepository
import com.scrumpokerpro.repository.GroupInviteRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class GroupInviteServiceImpl(
    val groupInviteRepository: GroupInviteRepository,
    val groupInviteMemberRepository: GroupInviteMemberRepository
) : GroupInviteService {

    @Transactional
    override suspend fun createGroupInvite(userId: UUID, createGroupInviteDto: CreateGroupInviteDto): GroupInviteDto {
        val now = LocalDateTime.now()
        val groupInvite = GroupInvite(
            name = createGroupInviteDto.name,
            userId = userId,
            created = now,
            modified = now
        ).let {
            groupInviteRepository.save(it)
        }
        createGroupInviteDto.emails.forEach { email ->
            GroupInviteMember(
                email = email,
                groupInviteId = groupInvite.id!!,
                created = now,
                modified = now
            ).let {
                groupInviteMemberRepository.save(it)
            }
        }
        return GroupInviteDto(
            id = groupInvite.id!!,
            name = groupInvite.name,
            emails = createGroupInviteDto.emails
        )
    }

    @Transactional
    override suspend fun updateGroupInvite(userId: UUID, groupInviteId: UUID, updateGroupInviteDto: UpdateGroupInviteDto): GroupInviteDto {
        val now = LocalDateTime.now()
        val groupInvite = groupInviteRepository.findById(groupInviteId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        groupInviteRepository.save(
            groupInvite.copy(
                name = updateGroupInviteDto.name,
                modified = now
            )
        )
        groupInviteMemberRepository.deleteAll(groupInviteMemberRepository.findByGroupInviteId(groupInviteId))
        updateGroupInviteDto.emails.forEach { email ->
            GroupInviteMember(
                email = email,
                groupInviteId = groupInvite.id!!,
                created = now,
                modified = now
            ).let {
                groupInviteMemberRepository.save(it)
            }
        }
        return GroupInviteDto(
            id = groupInvite.id!!,
            name = updateGroupInviteDto.name,
            emails = updateGroupInviteDto.emails
        )
    }

    override suspend fun getGroupInvites(userId: UUID): List<GroupInviteDto> {
        return groupInviteRepository.findByUserId(userId).map {
            GroupInviteDto(
                id = it.id!!,
                name = it.name,
                emails = groupInviteMemberRepository.findByGroupInviteId(it.id).map { it.email }
            )
        }
    }

    @Transactional
    override suspend fun deleteGroupInvite(userId: UUID, groupInviteId: UUID) {
        groupInviteRepository.findById(groupInviteId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        groupInviteMemberRepository.deleteAll(groupInviteMemberRepository.findByGroupInviteId(groupInviteId))
        groupInviteRepository.deleteById(groupInviteId)
    }

    override suspend fun getGroupInviteMembers(userId: UUID, groupInviteId: UUID): List<GroupInviteMember> {
        groupInviteRepository.findById(groupInviteId)?.also { meeting ->
            if (meeting.userId != userId) throw AccessDeniedException("Forbidden")
        } ?: throw EntityNotFoundException()
        return groupInviteMemberRepository.findByGroupInviteId(groupInviteId)
    }
}
