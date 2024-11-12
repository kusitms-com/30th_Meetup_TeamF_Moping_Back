package com.ping.application.place

import com.ping.application.place.dto.GeocodePlace
import com.ping.application.place.dto.SearchPlace
import com.ping.client.naver.place.NaverApiClient
import com.ping.domain.nonmember.repository.SubwayRepository
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val naverApiClient: NaverApiClient,
    private val subwayRepository: SubwayRepository
) {

    fun searchPlace(keyword: String): List<SearchPlace.Response> {
        var query = keyword
        subwayRepository.findByStation(keyword)?.let { query = "${keyword}역" }
        return naverApiClient.searchPlaces(query).map {
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
        val roundedPx = String.format("%.4f", px).toDouble()
        val roundedPy = String.format("%.4f", py).toDouble()
        return naverApiClient.getReverseGeocode(roundedPx, roundedPy)
    }
}