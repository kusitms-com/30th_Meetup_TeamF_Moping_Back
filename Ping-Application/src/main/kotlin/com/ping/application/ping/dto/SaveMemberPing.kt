package com.ping.application.ping.dto

interface SaveMemberPing {
    data class Request(
        val nonMemberId: Long,
        val sid: String
    )
}