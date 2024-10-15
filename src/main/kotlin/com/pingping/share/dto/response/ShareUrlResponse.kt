package com.pingping.share.dto.response

data class ShareUrlResponse (
    val id: Long,
    val shareUrl: String,
    val eventName: String,
    val neighborhood: String
)