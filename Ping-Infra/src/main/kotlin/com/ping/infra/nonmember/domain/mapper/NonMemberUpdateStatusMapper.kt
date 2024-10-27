package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.NonMemberUpdateStatusDomain
import com.ping.infra.nonmember.domain.jpa.entity.NonMemberUpdateStatusEntity

object NonMemberUpdateStatusMapper {

    fun toDomain(entity: NonMemberUpdateStatusEntity) = NonMemberUpdateStatusDomain(
        id = entity.id,
        nonMemberDomain = NonMemberMapper.toDomain(entity.nonMember),
        friendId = entity.friendId,
        isUpdate = entity.isUpdate
    )

    fun toEntity(domain: NonMemberUpdateStatusDomain) = NonMemberUpdateStatusEntity(
        id = domain.id,
        nonMember = NonMemberMapper.toEntity(domain.nonMemberDomain),
        friendId = domain.friendId,
        isUpdate = domain.isUpdate
    )
}