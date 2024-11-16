package com.ping.application.nonmember.dto

interface CreateNonMember {
    data class Request(
        val uuid: String,
        val name: String,
        val password: String,
        val bookmarkUrls: List<String>,
        val storeUrls: List<String>
    )
}