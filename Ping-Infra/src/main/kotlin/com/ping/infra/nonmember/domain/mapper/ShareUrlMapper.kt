package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.ShareUrlDomain
import com.ping.infra.nonmember.domain.jpa.entity.ShareUrlEntity

object ShareUrlMapper {

    fun toEntity(shareUrlDomain: ShareUrlDomain) = ShareUrlEntity(
        shareUrlDomain.id,
        shareUrlDomain.url,
        shareUrlDomain.eventName,
        shareUrlDomain.neighborhood,
        shareUrlDomain.px,
        shareUrlDomain.py,
        shareUrlDomain.uuid,
        shareUrlDomain.pingUpdateTime,
    )

    fun toDomain(shareUrlEntity: ShareUrlEntity) = ShareUrlDomain(
        shareUrlEntity.id,
        shareUrlEntity.url,
        shareUrlEntity.eventName,
        shareUrlEntity.neighborhood,
        shareUrlEntity.px,
        shareUrlEntity.py,
        shareUrlEntity.uuid,
        shareUrlEntity.pingUpdateTime,
    )
}