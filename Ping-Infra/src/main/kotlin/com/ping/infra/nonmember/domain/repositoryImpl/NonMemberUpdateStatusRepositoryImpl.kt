package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.NonMemberUpdateStatusDomain
import com.ping.domain.nonmember.repository.NonMemberUpdateStatusRepository
import org.springframework.stereotype.Repository

@Repository
class NonMemberUpdateStatusRepositoryImpl(
    private val jpaRepository: NonMemberUpdateStatusJpaRepository
) : NonMemberUpdateStatusRepository {

    override fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberUpdateStatusDomain> {
        return jpaRepository.findAllByNonMemberId(nonMemberId).map { NonMemberUpdateStatusMapper.toDomain(it) }
    }

    override fun saveAll(statuses: List<NonMemberUpdateStatusDomain>): List<NonMemberUpdateStatusDomain> {
        val entities = statuses.map { NonMemberUpdateStatusMapper.toEntity(it) }
        return jpaRepository.saveAll(entities).map { NonMemberUpdateStatusMapper.toDomain(it) }
    }
}