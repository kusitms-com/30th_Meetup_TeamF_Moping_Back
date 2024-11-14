package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.RecommendPlaceDomain
import com.ping.infra.nonmember.domain.jpa.entity.RecommendPlaceEntity

object RecommendPlaceMapper {
    fun toDomain(recommendPlaceEntity: RecommendPlaceEntity) = RecommendPlaceDomain(
        id = recommendPlaceEntity.id,
        shareUrlDomain = ShareUrlMapper.toDomain(recommendPlaceEntity.shareUrl),
        sid = recommendPlaceEntity.sid,
    )

    fun toEntity(recommendPlaceDomain: RecommendPlaceDomain) = RecommendPlaceEntity(
        id = recommendPlaceDomain.id,
        shareUrl = ShareUrlMapper.toEntity(recommendPlaceDomain.shareUrlDomain),
        sid = recommendPlaceDomain.sid,
    )
}