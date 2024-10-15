package com.pingping.member.dto.request

data class NonMemberLoginRequest(
    val urlId: String,
    val name: String,
    val password: String? = null
)
