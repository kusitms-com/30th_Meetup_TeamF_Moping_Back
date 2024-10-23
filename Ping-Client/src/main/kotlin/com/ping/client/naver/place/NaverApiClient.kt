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
}
