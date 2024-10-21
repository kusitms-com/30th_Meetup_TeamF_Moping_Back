package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.NonMemberBookmarkUrlDomain
import com.ping.domain.nonmember.repository.NonMemberBookmarkUrlRepository
import com.ping.infra.nonmember.domain.jpa.repository.NonMemberBookmarkUrlJpaRepository
import com.ping.infra.nonmember.domain.mapper.NonMemberBookmarkUrlMapper
import org.springframework.stereotype.Repository

@Repository
class NonMemberBookmarkUrlRepositoryImpl(
    private val nonMemberBookmarkUrlJpaRepository: NonMemberBookmarkUrlJpaRepository
) : NonMemberBookmarkUrlRepository {
    override fun saveAll(nonMemberBookmarkUrlDomains: List<NonMemberBookmarkUrlDomain>): List<NonMemberBookmarkUrlDomain> {
        return nonMemberBookmarkUrlJpaRepository.saveAll(nonMemberBookmarkUrlDomains.map {
            NonMemberBookmarkUrlMapper.toEntity(it)
        }).map { NonMemberBookmarkUrlMapper.toDomain(it) }
    }
}