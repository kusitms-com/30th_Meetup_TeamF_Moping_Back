package com.ping.api.place

import com.ping.application.place.PlaceService
import com.ping.application.place.dto.SearchPlace
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
    fun searchPlace(@RequestBody request: SearchPlace.Request): List<SearchPlace.Response> {
        return placeService.searchPlace(request);
    }

}