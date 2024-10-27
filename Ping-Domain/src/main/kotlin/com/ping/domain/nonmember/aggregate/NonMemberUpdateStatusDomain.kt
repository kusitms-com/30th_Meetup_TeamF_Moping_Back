package com.ping.domain.nonmember.aggregate

data class NonMemberUpdateStatusDomain(
    val id: Long,
    val nonMemberId: Long,
    val friendId: Long,
    var isUpdate: Boolean
) {
    companion object {
        fun of(nonMemberId: Long, friendId: Long, isUpdate: Boolean) = NonMemberUpdateStatusDomain(
            id = 0L,
            nonMemberId = nonMemberId,
            friendId = friendId,
            isUpdate = isUpdate
        )
    }
}