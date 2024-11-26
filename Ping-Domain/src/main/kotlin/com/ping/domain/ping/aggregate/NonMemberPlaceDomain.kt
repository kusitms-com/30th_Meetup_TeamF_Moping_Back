package com.ping.domain.ping.aggregate

import com.ping.domain.member.aggregate.NonMemberDomain

data class NonMemberPlaceDomain(
    val id: Long,
    val nonMember: NonMemberDomain,
    val sid: String,
    val placeType: PlaceType
) {
    companion object {
        fun of(nonMember: NonMemberDomain, sid: String, placeType: PlaceType) =
            NonMemberPlaceDomain(
                id = 0L,
                nonMember = nonMember,
                sid = sid,
                placeType = placeType
            )
    }
}