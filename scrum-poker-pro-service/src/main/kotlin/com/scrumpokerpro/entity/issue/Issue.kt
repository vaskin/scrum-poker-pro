package com.scrumpokerpro.entity.issue

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.UUID

data class Issue(
    @Id
    val id: UUID? = null,
    val jiraId: String? = null,
    val jiraParentId: String? = null,
    val key: String? = null,
    val link: String? = null,
    val title: String,
    val storyPoint: String? = null,
    val votingStatus: VotingStatus = VotingStatus.NEW,
    val synchronized: Boolean = false,
    val description: String? = null,
    val status: String? = null,
    val type: String,
    val iconUrl: String? = null,
    val subtask: Boolean = false,
    val parentId: UUID? = null,
    val meetingId: UUID,
    val fromIssueId: UUID? = null,
    val userId: UUID,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
