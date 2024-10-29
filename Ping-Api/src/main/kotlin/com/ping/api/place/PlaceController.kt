package com.ping.api.place

import com.ping.application.event.dto.CreateEvent
import com.ping.application.place.PlaceService
import com.ping.application.place.dto.SearchPlace
import com.ping.common.exception.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeService: PlaceService
) {

    // 장소 검색 API
    @GetMapping("/search/{keyword}")
    fun searchPlace(@PathVariable("keyword") keyword: String): ResponseEntity<SuccessResponse<List<SearchPlace.Response>>> {
        val response = placeService.searchPlace(keyword);
        return ResponseEntity.ok(
            SuccessResponse.of(HttpStatus.OK, "장소 검색에 성공하였습니다.", response)
        )
    }

}