package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.NonMemberDomain
import com.ping.infra.nonmember.domain.jpa.entity.NonMemberEntity

object NonMemberMapper {

    fun toDomain(nonMemberEntity: NonMemberEntity) = NonMemberDomain(
        nonMemberEntity.id,
        nonMemberEntity.name,
        nonMemberEntity.password,
        nonMemberEntity.profileSvg,
        nonMemberEntity.profileLockSvg,
        ShareUrlMapper.toDomain(nonMemberEntity.shareUrl)
    )

    fun toEntity(nonMemberDomain: NonMemberDomain) = NonMemberEntity(
        nonMemberDomain.id,
        nonMemberDomain.name,
        nonMemberDomain.password,
        nonMemberDomain.profileSvg,
        nonMemberDomain.profileLockSvg,
        ShareUrlMapper.toEntity(nonMemberDomain.shareUrlDomain)
    )
}