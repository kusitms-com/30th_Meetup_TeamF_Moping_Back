package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.SubwayDomain
import com.ping.domain.nonmember.repository.SubwayRepository
import com.ping.infra.nonmember.domain.mapper.SubwayMapper
import com.ping.infra.nonmember.domain.mongo.repository.SubwayMongoRepository
import org.springframework.stereotype.Repository

@Repository
class SubwayRepositoryImpl (
    private val subwayMongoRepository: SubwayMongoRepository
) : SubwayRepository {
    override fun findByStation(station: String): SubwayDomain? {
        return subwayMongoRepository.findByStation(station)?.let { SubwayMapper.toDomain(it) }
    }
}