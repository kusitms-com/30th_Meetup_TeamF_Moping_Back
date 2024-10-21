package com.ping.domain.nonmember.aggregate

class NonMemberBookmarkUrlDomain(
    val id: Long,
    val nonMember: NonMemberDomain,
    val bookmarkUrl: String
) {
    companion object {
        fun of(nonMember: NonMemberDomain, bookmarkUrls: List<String>) =
            bookmarkUrls.map { NonMemberBookmarkUrlDomain(0L, nonMember, it) }
    }
}