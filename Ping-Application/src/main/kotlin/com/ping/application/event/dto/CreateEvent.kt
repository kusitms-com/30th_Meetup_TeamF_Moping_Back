package com.ping.application.event.dto

class CreateEvent {
    data class Request(
        val neighborhood: String,
        val px: Double,
        val py: Double,
        val eventName: String
    )
    data class Response(
        val shareUrl: String
    )
}