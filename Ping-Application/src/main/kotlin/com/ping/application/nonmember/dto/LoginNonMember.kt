package com.ping.application.nonmember.dto

class LoginNonMember {
    data class Request(
        val nonMemberId: Long,
        val password: String
    )
    data class Response(
        val nonMemberId: Long,
        val name: String,
        val bookmarkUrls: List<String>,
        val storeUrls: List<String>
    )

}