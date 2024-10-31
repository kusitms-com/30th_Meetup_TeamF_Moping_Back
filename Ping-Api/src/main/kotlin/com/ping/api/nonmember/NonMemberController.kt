package com.ping.api.nonmember

import com.ping.application.nonmember.NonMemberLoginService
import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.application.nonmember.dto.GetNonMemberPing
import com.ping.application.nonmember.dto.LoginNonMember
import com.ping.application.nonmember.dto.UpdateNonMemberPings
import com.ping.common.exception.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class NonMemberController(
    private val nonMemberService: NonMemberService,
    private val nonMemberLoginService: NonMemberLoginService
) {
    @PutMapping(NonMemberApi.LOGIN)
    fun loginNonMember(@RequestBody request: LoginNonMember.Request): LoginNonMember.Response {
        return nonMemberLoginService.login(request)
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

    @PutMapping(NonMemberApi.PING)
    fun updateNonMemberPings(@RequestBody request: UpdateNonMemberPings.Request) {
        nonMemberService.updateNonMemberPings(request)
    }

    @GetMapping(NonMemberApi.PING_REFRESH_ALL)
    fun refreshAllNonMemberPings(@RequestParam uuid: String): GetAllNonMemberPings.Response {
        return nonMemberService.refreshAllNonMemberPings(uuid)
    }
}