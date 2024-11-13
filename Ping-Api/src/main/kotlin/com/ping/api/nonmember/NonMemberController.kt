package com.ping.api.nonmember

import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.*
import org.springframework.web.bind.annotation.*

@RestController
class NonMemberController(
    private val nonMemberService: NonMemberService
) {
    @PutMapping(NonMemberApi.LOGIN)
    fun loginNonMember(@RequestBody request: LoginNonMember.Request): LoginNonMember.Response {
        return nonMemberService.login(request)
    }

    @PostMapping(NonMemberApi.PING)
    fun createNonMemberPings(@RequestBody request: CreateNonMember.Request) {
        return nonMemberService.createNonMemberPings(request)
    }

    @GetMapping(NonMemberApi.PING)
    fun getNonMemberPings(@RequestParam uuid: String): GetAllNonMemberPings.Response {
        return nonMemberService.getAllNonMemberPings(uuid)
    }
    
    @GetMapping(NonMemberApi.PING_NONMEMBERID)
    fun getNonMemberPing(@PathVariable nonMemberId: Long): GetNonMemberPing.Response {
        return nonMemberService.getNonMemberPing(nonMemberId)
    }

    @GetMapping(NonMemberApi.PING_RECOMMEND)
    fun getRecommendPings(@RequestBody request: GetRecommendPings.Request): GetRecommendPings.Response {
        return nonMemberService.getRecommendPings(request)
    }

    @PutMapping(NonMemberApi.PING)
    fun updateNonMemberPings(@RequestBody request: UpdateNonMemberPings.Request) {
        nonMemberService.updateNonMemberPings(request)
    }

    @GetMapping(NonMemberApi.PING_REFRESH_ALL)
    fun refreshAllNonMemberPings(@RequestParam uuid: String): GetAllNonMemberPings.Response {
        return nonMemberService.refreshAllNonMemberPings(uuid)
    }
}