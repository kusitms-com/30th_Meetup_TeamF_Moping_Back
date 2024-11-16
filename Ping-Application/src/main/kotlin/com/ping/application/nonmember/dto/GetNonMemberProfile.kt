package com.ping.application.nonmember.dto

interface GetNonMemberProfile {
    data class Response (
        val name: String,
        val profileSvg: String,
        val profileLockSvg: String,
    )
}