package com.ping.application.place

import com.ping.application.place.dto.SavePlace
import com.ping.application.place.dto.SearchPlace
import com.ping.client.naver.place.NaverApiClient
import com.ping.domain.nonmember.aggregate.PlaceDomain
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceService(
    private val naverApiClient: NaverApiClient,
) {

    // 장소 검색
    fun searchPlace(keyword: String): List<SearchPlace.Response> {
        return naverApiClient.searchPlaces(keyword).map {
            SearchPlace.Response(
                name = it.title.replace("<b>", "").replace("</b>", ""),
                address = it.address,
                latitude = it.mapx,
                longitude = it.mapy
            )
        }
    }

    // 장소 저장
    @Transactional
    fun savePlace(request: SavePlace.Request) {
        val place = PlaceDomain(
            name = request.name,
            address = request.address,
            latitude = request.latitude,
            longitude = request.longitude
        )
    }
}