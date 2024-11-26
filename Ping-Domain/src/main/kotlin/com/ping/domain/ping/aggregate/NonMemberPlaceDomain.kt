package com.ping.domain.ping.aggregate

import com.ping.domain.member.aggregate.NonMemberDomain

data class NonMemberPlaceDomain(
    val id: Long,
    val nonMember: NonMemberDomain,
    val sid: String
) {
    companion object {
        fun of(nonMember: NonMemberDomain, sid: String) = NonMemberPlaceDomain(0L, nonMember, sid)
    }
}