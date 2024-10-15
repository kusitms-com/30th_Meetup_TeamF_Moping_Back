package com.pingping.member.dto.request

data class NonMemberLoginRequest(
    val shareUrlId: Long,
    val name: String,
    val password: String? = null
)
