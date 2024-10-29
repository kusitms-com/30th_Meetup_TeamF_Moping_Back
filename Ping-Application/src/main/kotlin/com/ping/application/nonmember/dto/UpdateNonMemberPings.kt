package com.ping.application.nonmember.dto

class UpdateNonMemberPings {
    data class Request(
        val nonMemberId: Long,
        val bookmarkUrls: List<String>,
        val storeUrls: List<String>
    )
}