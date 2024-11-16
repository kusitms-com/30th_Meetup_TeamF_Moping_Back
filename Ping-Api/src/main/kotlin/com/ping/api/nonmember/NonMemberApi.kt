package com.ping.api.nonmember

object NonMemberApi {
    const val BASE_URL = "/nonmembers"
    const val NONMEMBER_ID = "$BASE_URL/{nonMemberId}"
    const val LOGIN = "$BASE_URL/login"
    const val PING = "$BASE_URL/pings"
    const val PING_NONMEMBER_ID = "$PING/{nonMemberId}"
    const val PING_REFRESH_ALL = "$PING/refresh-all"
    const val PING_RECOMMEND = "$PING/recommend"
}