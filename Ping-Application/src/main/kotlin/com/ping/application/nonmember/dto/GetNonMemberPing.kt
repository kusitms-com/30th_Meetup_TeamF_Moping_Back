package com.ping.application.nonmember.dto

class GetNonMemberPing {
    data class Response(
        val pings: List<Ping>
    )

    data class Ping(
        val url: String,
        val placeName: String,
        val px: Double,
        val py: Double
    )
}