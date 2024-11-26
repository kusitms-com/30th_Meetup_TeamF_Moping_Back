package com.ping.infra.ping.mapper

import com.ping.domain.ping.aggregate.NonMemberPlaceDomain
import com.ping.infra.member.mapper.NonMemberMapper
import com.ping.infra.ping.jpa.entity.NonMemberPlaceEntity

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