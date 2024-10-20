package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.NonMemberPlaceDomain
import com.ping.infra.nonmember.domain.jpa.entity.NonMemberPlaceEntity

object NonMemberPlaceMapper {

    fun toDomain(nonMemberPlaceEntity: NonMemberPlaceEntity) = NonMemberPlaceDomain(
        nonMemberPlaceEntity.id,
        NonMemberMapper.toDomain(nonMemberPlaceEntity.nonMember),
        nonMemberPlaceEntity.sid
    )


    fun toEntity(nonMemberPlaceDomain: NonMemberPlaceDomain) = NonMemberPlaceEntity(
        nonMemberPlaceDomain.id,
        NonMemberMapper.toEntity(nonMemberPlaceDomain.nonMember),
        nonMemberPlaceDomain.sid
    )
}