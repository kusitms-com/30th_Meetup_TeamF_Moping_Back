package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.ProfileDomain
import com.ping.infra.nonmember.domain.mongo.entity.ProfileEntity

object ProfileMapper {

    fun toDomain(profileEntity: ProfileEntity) = ProfileDomain(
        profileEntity.id,
        profileEntity.url
    )

    fun toEntity(profileDomain: ProfileDomain) = ProfileEntity(
        profileDomain.id,
        profileDomain.url
    )
}