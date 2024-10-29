package com.ping.application.nonmember.dto

class GetAllNonMemberPings {
    data class Response(
        val eventName: String,
        val px: Double,
        val py: Double,
        val nonMembers: List<NonMember>,
        val pings: List<Ping>
    )

    data class NonMember(
        val nonMemberId: Long,
        val name : String
    )

    data class Ping(
        val iconLevel: Int,
        val nonMembers: List<NonMember>,
        val url: String,
        val placeName: String,
        val px: Double,
        val py: Double
    )
}