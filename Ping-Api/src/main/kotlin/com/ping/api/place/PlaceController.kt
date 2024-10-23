package com.ping.api.place

import com.ping.application.place.PlaceService
import com.ping.application.place.dto.SearchPlace
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeService: PlaceService
) {

    // 장소 검색 API
    @GetMapping("/search/{keyword}")
    fun searchPlace(@PathVariable("keyword") keyword: String): List<SearchPlace.Response> {
        return placeService.searchPlace(keyword);
    }

}