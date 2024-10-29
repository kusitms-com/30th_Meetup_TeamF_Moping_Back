package com.ping.client.naver.place

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NaverApiClient(
    @Value("\${naver.client.id}") private val clientId: String,
    @Value("\${naver.client.secret}") private val clientSecret: String
) {

    fun searchPlaces(keyword: String): List<NaverApiResponse.NaverPlace> {
        val client = WebClient.create("https://openapi.naver.com/v1/search/local.json")
        val response = client.get()
            .uri { uriBuilder ->
                uriBuilder.queryParam("query", keyword).queryParam("display", 10).build()
            }
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .bodyToMono(NaverApiResponse.NaverResponse::class.java)
            .block()

        return response?.items ?: emptyList()
    }

    fun getGeocodeAddress(address: String): Pair<Double?, Double?> {
        val client = WebClient.create("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode")
        val response = client.get()
            .uri { uriBuilder -> uriBuilder.queryParam("query", address).build() }
            .header("X-NCP-APIGW-API-KEY-ID", clientId)
            .header("X-NCP-APIGW-API-KEY", clientSecret)
            .retrieve()
            .bodyToMono(NaverApiResponse.NaverGeocodeResponse::class.java)
            .block()

        val location = response?.addresses?.firstOrNull()
        return if (location != null) {
            Pair(location.y, location.x)
        } else {
            Pair(null, null)
        }
    }
}
