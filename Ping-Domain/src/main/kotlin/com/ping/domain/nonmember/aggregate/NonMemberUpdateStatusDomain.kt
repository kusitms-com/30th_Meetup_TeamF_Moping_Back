package com.ping.domain.nonmember.aggregate

data class NonMemberUpdateStatusDomain(
    val id: Long,
    val nonMemberDomain: NonMemberDomain,
    val friendId: Long,
    var isUpdate: Boolean
) {
    companion object {
        fun of(nonMemberDomain: NonMemberDomain, friendId: Long, isUpdate: Boolean) = NonMemberUpdateStatusDomain(
            id = 0L,
            nonMemberDomain = nonMemberDomain,
            friendId = friendId,
            isUpdate = isUpdate
        )
    }
}