package com.ping.api.nonmember

import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/nonmembers")
class NonMemberController(
    private val nonMemberService: NonMemberService,
) {

    @PostMapping("/pings")
    fun createNonMemberPings(@RequestBody request: CreateNonMember.Request) {
        return nonMemberService.createNonMemberPings(request)

    }

    @GetMapping("/pings")
    fun getNonMemberPings(@RequestParam uuid: String): GetAllNonMemberPings.Response {
        return nonMemberService.getAllNonMemberPings(uuid)
    }
}