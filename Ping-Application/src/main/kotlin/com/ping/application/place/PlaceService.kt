package com.ping.application.place

import com.ping.application.place.dto.PlaceRequestDto
import com.ping.application.place.dto.SavePlace
import com.ping.application.place.dto.SearchPlace
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceService(
    private val naverApiClient: NaverApiClient,
    private val placeRepository: PlaceRepository
) {

    // 장소 검색
    fun searchPlace(request: SearchPlace.Request): List<SearchPlace.Response> {
        return naverApiClient.searchPlaces(request.keyword).map {
            SearchPlace.Response(
                name = it.name,
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude
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
        placeRepository.save(place)
    }
}