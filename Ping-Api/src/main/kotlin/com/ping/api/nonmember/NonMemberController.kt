package com.ping.api.nonmember

import com.ping.application.nonmember.NonMemberLoginService
import com.ping.application.nonmember.NonMemberService
import com.ping.application.nonmember.dto.CreateNonMember
import com.ping.application.nonmember.dto.GetAllNonMemberPings
import com.ping.application.nonmember.dto.LoginNonMember
import com.ping.common.exception.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class NonMemberController(
    private val nonMemberService: NonMemberService,
    private val nonMemberLoginService: NonMemberLoginService
) {
    @PutMapping("/nonmembers/login")
    fun loginNonMember(
        @RequestBody request: LoginNonMember.Request
    ): ResponseEntity<SuccessResponse<Unit>> {
        nonMemberLoginService.login(request)

        return ResponseEntity.ok(
            SuccessResponse.of(HttpStatus.OK, "비회원 로그인 성공")
        )
    }

    @PostMapping(NonMemberApi.PING)
    fun createNonMemberPings(@RequestBody request: CreateNonMember.Request) {
        return nonMemberService.createNonMemberPings(request)
    }

    @GetMapping(NonMemberApi.PING)
    fun getNonMemberPings(@RequestParam uuid: String): GetAllNonMemberPings.Response {
        return nonMemberService.getAllNonMemberPings(uuid)
    }
}