package com.pingping.member.application

import com.pingping.global.exception.CustomException
import com.pingping.global.exception.ExceptionContent
import com.pingping.member.domain.repository.NonMemberRepository
import com.pingping.member.dto.request.NonMemberCreateRequest
import com.pingping.member.dto.request.NonMemberLoginRequest
import com.pingping.member.dto.response.NonMemberLoginResponse
import com.pingping.share.domain.repository.ShareUrlRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NonMemberService(
    private val nonMemberRepository: NonMemberRepository,
    private val shareUrlRepository: ShareUrlRepository,
    private val nonMemberMapper: NonMemberMapper
) {
    fun loginNonMember(request: NonMemberLoginRequest): NonMemberLoginResponse {
        // URL ID와 name으로 NonMember 조회
        val nonMember = nonMemberRepository.findByShareUrlIdAndName(request.shareUrlId, request.name)
            ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        // 비밀번호가 존재한다면 비교
        if (request.password != null && request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }

        return NonMemberLoginResponse(nonMember.id)
    }

    @Transactional
    fun createNonMember(request: NonMemberCreateRequest): Long {
        // ShareUrl 존재 여부 확인
        val shareUrl = shareUrlRepository.findById(request.shareUrlId)
            .orElseThrow { CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND) }

        // shareUrlId과 name으로 비회원 존재 여부 확인
        nonMemberRepository.findByShareUrlIdAndName(request.shareUrlId, request.name)?.let {
            throw CustomException(ExceptionContent.NON_MEMBER_ALREADY_EXISTS)
        }

        val nonMember = nonMemberMapper.toEntity(request, shareUrl)
        val savedNonMember = nonMemberRepository.save(nonMember)

        return savedNonMember.id
    }

}