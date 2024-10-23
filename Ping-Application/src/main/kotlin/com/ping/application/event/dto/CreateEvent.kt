package com.ping.application.event.dto

class CreateEvent {
    data class Request(
        val neighborhood: String,
        val mapx: Double,
        val mapy: Double,
        val eventName: String
    )
    data class Response(
        val shareUrl: String
    )
}