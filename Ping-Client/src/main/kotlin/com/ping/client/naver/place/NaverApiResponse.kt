package com.ping.client.naver.place

class NaverApiResponse {
    data class NaverResponse(
        val items: List<NaverPlace>
    )

    data class NaverPlace(
        val title: String,
        val address: String,
        val mapx: Double,
        val mapy: Double
    )
}