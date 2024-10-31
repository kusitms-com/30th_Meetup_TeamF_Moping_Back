package com.ping.application.place

import com.ping.application.place.dto.GeocodePlace
import com.ping.application.place.dto.SearchPlace
import com.ping.client.naver.place.NaverApiClient
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val naverApiClient: NaverApiClient,
) {

    fun searchPlace(keyword: String): List<SearchPlace.Response> {
        return naverApiClient.searchPlaces(keyword).map {
            SearchPlace.Response(
                name = it.title.replace("<b>", "").replace("</b>", ""),
                address = it.address,
                px = it.mapx,
                py = it.mapy
            )
        }
    }

    fun getGeocodeAddress(address: String): GeocodePlace.Response {
        val (latitude, longitude) = naverApiClient.getGeocodeAddress(address)
        return GeocodePlace.Response(
            address = address,
            px = latitude,
            py = longitude
        )
    }

    fun getReverseGeocode(px: Double, py: Double): String? {
        return naverApiClient.getReverseGeocode(px, py)
    }

}