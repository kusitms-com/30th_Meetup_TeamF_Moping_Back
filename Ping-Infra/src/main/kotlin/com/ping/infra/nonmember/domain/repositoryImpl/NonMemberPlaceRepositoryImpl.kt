package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.NonMemberPlaceDomain
import com.ping.domain.nonmember.repository.NonMemberPlaceRepository
import com.ping.infra.nonmember.domain.jpa.repository.NonMemberPlaceJpaRepository
import com.ping.infra.nonmember.domain.mapper.NonMemberPlaceMapper
import org.springframework.stereotype.Repository

@Repository
class NonMemberPlaceRepositoryImpl(
    private val nonMemberPlaceJpaRepository: NonMemberPlaceJpaRepository
) : NonMemberPlaceRepository {
    override fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain> {
        return nonMemberPlaceJpaRepository.saveAll(nonMemberPlaceDomains.map { NonMemberPlaceMapper.toEntity(it) })
            .map { NonMemberPlaceMapper.toDomain(it) }
    }
}