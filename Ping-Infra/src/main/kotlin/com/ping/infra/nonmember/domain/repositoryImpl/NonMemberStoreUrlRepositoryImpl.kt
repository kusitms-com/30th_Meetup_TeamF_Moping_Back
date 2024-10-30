package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.NonMemberStoreUrlDomain
import com.ping.domain.nonmember.repository.NonMemberStoreUrlRepository
import com.ping.infra.nonmember.domain.jpa.repository.NonMemberStoreUrlJpaRepository
import com.ping.infra.nonmember.domain.mapper.NonMemberStoreUrlMapper
import org.springframework.stereotype.Repository

@Repository
class NonMemberStoreUrlRepositoryImpl(
    private val nonMemberStoreUrlJpaRepository: NonMemberStoreUrlJpaRepository
) : NonMemberStoreUrlRepository {
    override fun saveAll(nonMemberStoreUrlDomains: List<NonMemberStoreUrlDomain>): List<NonMemberStoreUrlDomain> {
        return nonMemberStoreUrlJpaRepository.saveAll(nonMemberStoreUrlDomains.map { NonMemberStoreUrlMapper.toEntity(it) })
            .map { NonMemberStoreUrlMapper.toDomain(it) }
    }

    override fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberStoreUrlDomain> {
        return nonMemberStoreUrlJpaRepository.findAllByNonMemberId(nonMemberId)
            .map { NonMemberStoreUrlMapper.toDomain(it) }
    }
}