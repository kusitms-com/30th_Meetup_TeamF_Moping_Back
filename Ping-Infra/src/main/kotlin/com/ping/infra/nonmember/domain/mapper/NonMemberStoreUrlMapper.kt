package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.NonMemberStoreUrlDomain
import com.ping.infra.nonmember.domain.jpa.entity.NonMemberStoreUrlEntity

object NonMemberStoreUrlMapper {
    
    fun toDomain(nonMemberStoreUrlEntity: NonMemberStoreUrlEntity) = NonMemberStoreUrlDomain(
        nonMemberStoreUrlEntity.id,
        NonMemberMapper.toDomain(nonMemberStoreUrlEntity.nonMember),
        nonMemberStoreUrlEntity.storeUrl
    )

    fun toEntity(nonMemberStoreUrlDomain: NonMemberStoreUrlDomain) = NonMemberStoreUrlEntity(
        nonMemberStoreUrlDomain.id,
        NonMemberMapper.toEntity(nonMemberStoreUrlDomain.nonMember),
        nonMemberStoreUrlDomain.storeUrl
    )
}