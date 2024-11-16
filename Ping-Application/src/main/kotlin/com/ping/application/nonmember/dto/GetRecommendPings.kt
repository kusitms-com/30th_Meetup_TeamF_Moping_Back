package com.ping.application.nonmember.dto

interface GetRecommendPings {
    data class Response(
        val recommendPings: List<RecommendPing>,
    )

    data class RecommendPing(
        val sid: String,
        val placeName: String,
        val url: String,
        val px: Double,
        val py: Double,
        val type: String,
    )
}