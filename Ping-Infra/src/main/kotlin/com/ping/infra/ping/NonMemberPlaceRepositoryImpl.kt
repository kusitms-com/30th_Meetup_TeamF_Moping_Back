package com.ping.infra.ping

import com.ping.domain.ping.aggregate.NonMemberPlaceDomain
import com.ping.domain.ping.dto.SidCount
import com.ping.domain.ping.repository.NonMemberPlaceRepository
import com.ping.infra.ping.jpa.repository.NonMemberPlaceJpaRepository
import com.ping.infra.ping.mapper.NonMemberPlaceMapper
import org.springframework.stereotype.Repository

@Repository
class NonMemberPlaceRepositoryImpl(
    private val nonMemberPlaceJpaRepository: NonMemberPlaceJpaRepository
) : NonMemberPlaceRepository {
    override fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain> {
        return nonMemberPlaceJpaRepository.saveAll(nonMemberPlaceDomains.map { NonMemberPlaceMapper.toEntity(it) })
                .map { NonMemberPlaceMapper.toDomain(it) }
    }
    override fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberPlaceDomain> {
        return nonMemberPlaceJpaRepository.findAllByNonMemberId(nonMemberId).map { NonMemberPlaceMapper.toDomain(it) }
    }
    override fun deleteAll(ids: List<Long>) {
        nonMemberPlaceJpaRepository.deleteAllById(ids)
    }

    override fun findCountBySidIn(sids: List<String>): List<SidCount> {
        return nonMemberPlaceJpaRepository.findCountBySidIn(sids)
    }

    override fun findAllByNonMemberIdIn(nonMemberIds: List<Long>): List<NonMemberPlaceDomain> {
        return nonMemberPlaceJpaRepository.findAllByNonMemberIdIn(nonMemberIds)
                .map { NonMemberPlaceMapper.toDomain(it) }
    }
}