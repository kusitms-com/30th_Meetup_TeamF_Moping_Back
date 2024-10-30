package com.ping.api.place

import com.ping.application.event.dto.CreateEvent
import com.ping.application.place.PlaceService
import com.ping.application.place.dto.GeocodePlace
import com.ping.application.place.dto.SearchPlace
import com.ping.common.exception.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PlaceController(
    private val placeService: PlaceService
) {

    @GetMapping(PlaceApi.SEARCH)
    fun searchPlace(@RequestParam("keyword") keyword: String): ResponseEntity<SuccessResponse<List<SearchPlace.Response>>> {
        val response = placeService.searchPlace(keyword);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "장소 검색에 성공하였습니다.", response))
    }

    @GetMapping(PlaceApi.GEOCODE)
    fun geocodePlace(@RequestParam("address") address: String): ResponseEntity<SuccessResponse<GeocodePlace.Response>> {
        val response = placeService.getGeocodeAddress(address)
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "좌표 검색에 성공하였습니다.", response))
    }

}