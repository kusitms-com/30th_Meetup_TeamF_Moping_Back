package com.ping.application.place.dto

class SearchPlace {
    data class Response(
        val name: String,
        val address: String,
        val px: Double,
        val py: Double
    )
}