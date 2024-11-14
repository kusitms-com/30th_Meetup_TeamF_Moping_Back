package com.ping.domain.nonmember.aggregate

class RecommendPlaceDomain(
    val id: Long,
    val shareUrlDomain: ShareUrlDomain,
    val sid: String
) {
    companion object {
        fun of(shareUrl: ShareUrlDomain, sid: String) = RecommendPlaceDomain(
                id = 0L,
                shareUrlDomain = shareUrl,
                sid = sid,
            )
    }
}