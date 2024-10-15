package com.pingping.global.exception

import org.springframework.http.HttpStatus

enum class ExceptionContent(val httpStatus: HttpStatus, val message: String) {

    //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // NonMember 관련 예외
    NON_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 비회원 정보를 찾을 수 없습니다."),
    NON_MEMBER_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "비회원 로그인 실패: 비밀번호가 일치하지 않습니다.")
}