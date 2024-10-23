package com.ping.domain.nonmember.aggregate

data class PlaceDomain(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)