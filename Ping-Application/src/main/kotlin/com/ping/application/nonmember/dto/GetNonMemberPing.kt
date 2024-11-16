package com.ping.application.nonmember.dto

class GetNonMemberPing {
    data class Response(
        val pings: List<GetAllNonMemberPings.Ping>
    )
}