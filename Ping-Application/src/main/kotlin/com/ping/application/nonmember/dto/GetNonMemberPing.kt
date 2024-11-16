package com.ping.application.nonmember.dto

interface GetNonMemberPing {
    data class Response(
        val pings: List<GetAllNonMemberPings.Ping>
    )
}