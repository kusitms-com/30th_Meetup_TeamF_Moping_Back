package com.ping.domain.nonmember.aggregate

data class NonMemberPlaceDomain(
    val id: Long,
    val nonMember: NonMemberDomain,
    val sid: String
) {
    companion object {
        fun of(nonMember: NonMemberDomain, sid: String) = NonMemberPlaceDomain(0L, nonMember, sid)
    }
}