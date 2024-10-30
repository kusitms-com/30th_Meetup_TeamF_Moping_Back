package com.ping.application.nonmember

import com.ping.application.nonmember.dto.LoginNonMember
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.domain.nonmember.repository.NonMemberBookmarkUrlRepository
import com.ping.domain.nonmember.repository.NonMemberRepository
import com.ping.domain.nonmember.repository.NonMemberStoreUrlRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NonMemberLoginService(
    private val nonMemberRepository: NonMemberRepository,
    private val nonMemberBookmarkUrlRepository: NonMemberBookmarkUrlRepository,
    private val nonMemberStoreUrlRepository: NonMemberStoreUrlRepository,
    private val validator: NonMemberValidator
) {
    fun login(request: LoginNonMember.Request): LoginNonMember.Response {
        // 비밀번호 형식 검사 (4자리 숫자)
        validator.password(request.password)

        val nonMember = nonMemberRepository.findById(request.nonMemberId) ?: throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)

        // 비밀번호가 일치하는지 비교
        if (request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }

        // 비회원의 북마크 URL 리스트와 스토어 URL 리스트 조회
        val bookmarkUrls = nonMemberBookmarkUrlRepository.findAllByNonMemberId(request.nonMemberId).map { it.bookmarkUrl }
        val storeUrls = nonMemberStoreUrlRepository.findAllByNonMemberId(request.nonMemberId).map { it.storeUrl }

        // 로그인 응답 데이터 반환
        return LoginNonMember.Response(
            nonMemberId = nonMember.id,
            name = nonMember.name,
            bookmarkUrls = bookmarkUrls,
            storeUrls = storeUrls
        )
    }
}


