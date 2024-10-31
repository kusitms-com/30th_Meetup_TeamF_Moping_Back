package com.ping.api.nonmember

object NonMemberApi {
    const val BASE_URL = "/nonmembers"
    const val LOGIN = "$BASE_URL/login"
    const val PING = "$BASE_URL/pings"
    const val PING_NONMEMBERID = "$PING/{nonMemberId}"
    const val PING_REFRESH_ALL = "$PING/refresh-all"
}