package com.ping.api.member

import com.ping.application.member.dto.GetNonMemberProfile
import com.ping.application.member.dto.LoginNonMember
import com.ping.application.member.service.NonMemberService
import com.ping.application.ping.dto.*
import org.springframework.web.bind.annotation.*

@RestController
class NonMemberController(
    private val nonMemberService: NonMemberService
) {
    @PutMapping(NonMemberApi.LOGIN)
    fun loginNonMember(@RequestBody request: LoginNonMember.Request): LoginNonMember.Response {
        return nonMemberService.login(request)
    }
    @GetMapping(NonMemberApi.NONMEMBER_ID)
    fun getNonMemberProfile(@PathVariable nonMemberId: Long): GetNonMemberProfile.Response {
        return nonMemberService.getNonMemberProfile(nonMemberId)
    }
}