package com.ping.domain.nonmember.aggregate

data class ShareUrlDomain(
    val id: Long,
    val url: String,
    val eventName: String,
    val neighborhood: String,
    val uuid: String
)