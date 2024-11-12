package com.ping.api.ping

import com.ping.application.ping.dto.IsCorrectUrl
import com.ping.domain.ping.PingService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController(private val pingService: PingService) {

    @PostMapping(PingApi.BOOKMARK)
    fun isCorrectBookmarkUrl(@RequestBody isCorrectUrl: IsCorrectUrl.Request) {
        pingService.bookmarkUrlToBookmarks(isCorrectUrl.url)
    }

    @PostMapping(PingApi.STORE)
    fun isCorrectStoreUrl(@RequestBody isCorrectUrl: IsCorrectUrl.Request) {
        pingService.storeUrlToBookmark(isCorrectUrl.url)
    }
}