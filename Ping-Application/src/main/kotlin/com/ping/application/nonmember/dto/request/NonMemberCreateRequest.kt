package com.ping.application.nonmember.dto.request

data class NonMemberCreateRequest(
    val uuid: String,
    val name: String,
    val password: String,
    val bookmarkUrls: List<String>,
    val storeUrls: List<String>
)