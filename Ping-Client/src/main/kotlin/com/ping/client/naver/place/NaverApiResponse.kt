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

    data class Address(
        val roadAddress: String,
        val jibunAddress: String,
        val x: Double,  // 경도
        val y: Double   // 위도
    )

    data class NaverGeocodeResponse(
        val addresses: List<Address>
    )
}