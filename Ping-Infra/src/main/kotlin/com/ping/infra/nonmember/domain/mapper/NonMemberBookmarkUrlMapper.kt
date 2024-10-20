package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.NonMemberBookmarkUrlDomain
import com.ping.infra.nonmember.domain.jpa.entity.NonMemberBookmarkUrlEntity

object NonMemberBookmarkUrlMapper {

    fun toDomain(nonMemberBookUrlEntity: NonMemberBookmarkUrlEntity) = NonMemberBookmarkUrlDomain(
        nonMemberBookUrlEntity.id,
        NonMemberMapper.toDomain(nonMemberBookUrlEntity.nonMember),
        nonMemberBookUrlEntity.bookmarkUrl
    )

    fun toEntity(nonMemberBookUrlDomain: NonMemberBookmarkUrlDomain) = NonMemberBookmarkUrlEntity(
        nonMemberBookUrlDomain.id,
        NonMemberMapper.toEntity(nonMemberBookUrlDomain.nonMember),
        nonMemberBookUrlDomain.bookmarkUrl
    )
}