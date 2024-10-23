package com.ping.application.nonmember

import com.ping.common.exception.CustomException
import com.ping.common.exception.ExceptionContent
import org.springframework.stereotype.Component

@Component
class NamePasswordValidator {

    // 이름 유효성 검증
    fun name(name: String) {
        val namePattern = "^[가-힣a-zA-Z]{1,6}\$".toRegex()
        if (!namePattern.matches(name)) {
            throw CustomException(ExceptionContent.INVALID_NAME_FORMAT)
        }
    }

    // 비밀번호 유효성 검증
    fun password(password: String) {
        if (!password.matches(Regex("\\d{4}"))) {
            throw CustomException(ExceptionContent.INVALID_PASSWORD_FORMAT)
        }
    }
}
