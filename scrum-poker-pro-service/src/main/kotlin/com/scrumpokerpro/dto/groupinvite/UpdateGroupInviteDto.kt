package com.scrumpokerpro.dto.groupinvite

data class UpdateGroupInviteDto(
    val name: String,
    val emails: List<String> = listOf()
)
