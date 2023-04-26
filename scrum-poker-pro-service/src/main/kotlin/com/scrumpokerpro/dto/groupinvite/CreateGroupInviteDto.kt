package com.scrumpokerpro.dto.groupinvite

data class CreateGroupInviteDto(
    val name: String,
    val emails: List<String> = listOf()
)
