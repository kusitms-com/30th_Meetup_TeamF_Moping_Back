package com.ping.application.place.dto

class SavePlace {
    data class Request (
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    )
}