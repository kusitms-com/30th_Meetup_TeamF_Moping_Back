package com.ping.api.nonmember

object NonMemberApi {
    const val BASE_URL = "/nonmembers"
    const val PING = "$BASE_URL/pings"
    const val PING_NONMEMBERID = "$PING/{nonMemberId}"
}