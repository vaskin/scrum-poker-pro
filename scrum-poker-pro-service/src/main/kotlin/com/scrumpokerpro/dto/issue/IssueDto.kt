package com.scrumpokerpro.dto.issue

import com.scrumpokerpro.entity.issue.VotingStatus
import java.time.LocalDateTime
import java.util.UUID

data class IssueDto(
    val id: UUID? = null,
    val jiraId: String? = null,
    val key: String? = null,
    val link: String? = null,
    val title: String,
    val storyPoint: String? = null,
    val synchronized: Boolean = false,
    val description: String? = null,
    val status: String? = null,
    val type: String,
    val iconUrl: String? = null,
    val subtask: Boolean = false,
    val parentId: UUID? = null,
    val votingStatus: VotingStatus = VotingStatus.NEW,
    val child: List<IssueDto> = listOf(),
    val created: LocalDateTime,
    val modified: LocalDateTime
)
