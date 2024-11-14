package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.RecommendPlaceDomain
import com.ping.domain.nonmember.repository.RecommendPlaceRepository
import com.ping.infra.nonmember.domain.jpa.repository.RecommendPlaceJpaRepository
import com.ping.infra.nonmember.domain.mapper.RecommendPlaceMapper

class RecommendPlaceRepositoryImpl(
    private val recommendPlaceJpaRepository: RecommendPlaceJpaRepository
) : RecommendPlaceRepository {
    override fun saveAll(recommendPlaceDomains: List<RecommendPlaceDomain>) {
        recommendPlaceJpaRepository.saveAll(recommendPlaceDomains.map { RecommendPlaceMapper.toEntity(it) })
            .map { RecommendPlaceMapper.toDomain(it) }
    }

}