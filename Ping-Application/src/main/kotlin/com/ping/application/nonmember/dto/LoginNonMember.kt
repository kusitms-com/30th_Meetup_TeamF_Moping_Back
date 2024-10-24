package com.ping.application.nonmember.dto

class LoginNonMember {
    data class Request(
        val nonMemberId: Long,
        val password: String
    )
}