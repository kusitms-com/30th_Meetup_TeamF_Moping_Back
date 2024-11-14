package com.ping.application.nonmember.dto

interface SaveRecommendPings {
    data class Request(
        val uuid: String,
        val sids: List<String>,
    )
}