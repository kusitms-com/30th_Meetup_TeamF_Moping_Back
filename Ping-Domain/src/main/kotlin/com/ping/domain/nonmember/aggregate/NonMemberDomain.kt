package com.ping.domain.nonmember.aggregate

data class NonMemberDomain(
    val id: Long,
    val name: String,
    val password: String,
    val shareUrlDomain: ShareUrlDomain
) {
    companion object {
        fun of(name: String, password: String, shareUrlDomain: ShareUrlDomain) =
            NonMemberDomain(0L, name, password, shareUrlDomain)
    }
}