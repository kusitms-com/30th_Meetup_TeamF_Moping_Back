package com.ping.application.nonmember.dto

interface GetRecommendPings {
    data class Request(
        val px: Double,
        val py: Double,
        val radiusInKm: Double,
    )

    data class Response(
        val pings: List<RecommendPing>,
    )

    data class RecommendPing(
        val placeName: String,
        val url: String,
        val px: Double,
        val py: Double,
    )
}