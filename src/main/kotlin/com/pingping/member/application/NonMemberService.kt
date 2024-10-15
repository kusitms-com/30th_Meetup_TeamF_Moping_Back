package com.pingping.member.application

import com.pingping.global.exception.CustomException
import com.pingping.global.exception.ExceptionContent
import com.pingping.member.domain.repository.NonMemberRepository
import com.pingping.member.dto.request.NonMemberLoginRequest
import com.pingping.member.dto.response.NonMemberLoginResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NonMemberService(
    private val nonMemberRepository: NonMemberRepository
) {
    fun loginNonMember(request: NonMemberLoginRequest): NonMemberLoginResponse {
        // URL ID와 name으로 NonMember 조회
        val nonMember = nonMemberRepository.findByShareUrlIdAndName(
            urlId = request.urlId,
            name = request.name
        ) ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        // 비밀번호가 존재한다면 비교
        if (request.password != null && request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }

        return NonMemberLoginResponse(nonMemberId = nonMember.id)
    }
}