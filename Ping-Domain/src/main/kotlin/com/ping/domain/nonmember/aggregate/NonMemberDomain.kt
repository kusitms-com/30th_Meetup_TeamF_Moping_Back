package com.ping.domain.nonmember.aggregate

data class NonMemberDomain(
    val id: Long,
    val name: String,
    val password: String,
    val profileSvg: String,
    val profileLockSvg: String,
    val shareUrlDomain: ShareUrlDomain
) {
    companion object {
        fun of(
            name: String,
            password: String,
            profileSvg: String,
            profileLockSvg: String,
            shareUrlDomain: ShareUrlDomain
        ) = NonMemberDomain(0L, name, password, profileSvg, profileLockSvg, shareUrlDomain)
    }
}