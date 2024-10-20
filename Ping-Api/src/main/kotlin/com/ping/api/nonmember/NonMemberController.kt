package com.ping.api.nonmember

import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.request.NonMemberCreateRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/nonmembers")
class NonMemberController(
    private val nonMemberService: NonMemberService,
) {

    @PostMapping("/pings")
    fun createNonMemberPings(@RequestBody nonMemberCreateRequest: NonMemberCreateRequest) {
        return nonMemberService.createNonMemberPings(nonMemberCreateRequest)
    }
}