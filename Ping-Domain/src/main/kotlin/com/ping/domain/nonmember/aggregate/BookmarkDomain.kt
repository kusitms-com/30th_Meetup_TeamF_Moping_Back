package com.ping.domain.nonmember.aggregate

data class BookmarkDomain (
    val name: String,
    val px: Double,
    val py: Double,
    val sid: String,
    val address: String,
    val mcidName: String,
    val url: String
)