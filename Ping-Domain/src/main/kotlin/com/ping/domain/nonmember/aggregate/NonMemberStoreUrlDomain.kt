package com.ping.domain.nonmember.aggregate

data class NonMemberStoreUrlDomain(
    val id: Long,
    val nonMember: NonMemberDomain,
    val storeUrl: String
) {
    companion object {
        fun of(nonMemberDomain: NonMemberDomain, storeUrl: List<String>) =
            storeUrl.map { NonMemberStoreUrlDomain(0L, nonMemberDomain, it) }
    }
}