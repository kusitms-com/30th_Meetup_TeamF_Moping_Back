package com.ping.application.place

import com.ping.application.place.dto.GeocodePlace
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

    fun getGeocodeAddress(address: String): GeocodePlace.Response {
        val (latitude, longitude) = naverApiClient.getGeocodeAddress(address)
        return GeocodePlace.Response(
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    }

}