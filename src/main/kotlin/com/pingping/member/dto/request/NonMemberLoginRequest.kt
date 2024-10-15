package com.pingping.member.dto.request

data class NonMemberLoginRequest(
    val urlId: Long,
    val name: String,
    val password: String? = null
)
