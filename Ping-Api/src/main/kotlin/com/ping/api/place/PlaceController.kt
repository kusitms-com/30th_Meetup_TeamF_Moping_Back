package com.ping.api.place

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeService: PlaceService
) {

    // 장소 검색 API
    @PostMapping("/search")
    fun searchPlace(@RequestBody request: PlaceRequestDto.SearchRequest) =
        placeService.searchPlace(request)

}