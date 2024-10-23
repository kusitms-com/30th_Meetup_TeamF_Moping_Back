package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.ShareUrlDomain
import com.ping.infra.nonmember.domain.jpa.entity.ShareUrlEntity

object ShareUrlMapper {

    fun toEntity(shareUrlDomain: ShareUrlDomain) = ShareUrlEntity(
        shareUrlDomain.id,
        shareUrlDomain.url,
        shareUrlDomain.eventName,
        shareUrlDomain.neighborhood,
        shareUrlDomain.latitude,
        shareUrlDomain.longtitude,
        shareUrlDomain.uuid
    )

    fun toDomain(shareUrlEntity: ShareUrlEntity) = ShareUrlDomain(
        shareUrlEntity.id,
        shareUrlEntity.url,
        shareUrlEntity.eventName,
        shareUrlEntity.neighborhood,
        shareUrlEntity.latitude,
        shareUrlEntity.longtitude,
        shareUrlEntity.uuid
    )
}