package com.ping.application.nonmember

import com.ping.application.nonmember.dto.LoginNonMember
import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import com.ping.domain.nonmember.repository.NonMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NonMemberLoginService(
    private val nonMemberRepository: NonMemberRepository,
    private val validator: NamePasswordValidator
) {
    fun loginNonMember(request: LoginNonMember.Request) {
        // 비밀번호 형식 검사 (4자리 숫자)
        validator.password(request.password)

        val nonMember = nonMemberRepository.findById(request.nonMemberId)?:let {
            throw CustomException(ExceptionContent.NON_MEMBER_NOT_FOUND)
        }

        // 비밀번호가 일치하는지 비교
        if (request.password != nonMember.password) {
            throw CustomException(ExceptionContent.NON_MEMBER_LOGIN_FAILED)
        }
    }
}


