package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.NonMemberDomain
import com.ping.domain.nonmember.repository.NonMemberRepository
import com.ping.infra.nonmember.domain.jpa.repository.NonMemberJpaRepository
import com.ping.infra.nonmember.domain.mapper.NonMemberMapper
import org.springframework.stereotype.Repository

@Repository
class NonMemberRepositoryImpl(
    private val nonMemberJpaRepository: NonMemberJpaRepository,
) : NonMemberRepository {
    override fun findByShareUrlIdAndName(shareUrlId: Long, name: String): NonMemberDomain? {
        return nonMemberJpaRepository.findByShareUrlIdAndName(shareUrlId, name)?.let {
            NonMemberMapper.toDomain(it)
        }
    }

    override fun save(nonMemberDomain: NonMemberDomain): NonMemberDomain {
        return NonMemberMapper.toDomain(nonMemberJpaRepository.save(NonMemberMapper.toEntity(nonMemberDomain)))
    }
}