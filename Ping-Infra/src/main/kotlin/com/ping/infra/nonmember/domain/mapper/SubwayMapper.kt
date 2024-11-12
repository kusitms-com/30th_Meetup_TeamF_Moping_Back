package com.ping.infra.nonmember.domain.mapper

import com.ping.domain.nonmember.aggregate.SubwayDomain
import com.ping.infra.nonmember.domain.mongo.entity.SubwayEntity

object SubwayMapper {

    fun toEntity(subwayDomain: SubwayDomain) = SubwayEntity(
        subwayDomain.id,
        subwayDomain.station
    )

    fun toDomain(subwayEntity: SubwayEntity) = SubwayDomain(
        subwayEntity.id,
        subwayEntity.station
    )
}