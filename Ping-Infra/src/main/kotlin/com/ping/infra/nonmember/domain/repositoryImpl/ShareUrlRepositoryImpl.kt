package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.ShareUrlDomain
import com.ping.domain.nonmember.repository.ShareUrlRepository
import com.ping.infra.nonmember.domain.jpa.repository.ShareUrlJpaRepository
import com.ping.infra.nonmember.domain.mapper.ShareUrlMapper
import org.springframework.stereotype.Repository

@Repository
class ShareUrlRepositoryImpl(
    private val shareUrlJpaRepository: ShareUrlJpaRepository
) : ShareUrlRepository {
    override fun findByUuid(uuid: String): ShareUrlDomain? {
        return shareUrlJpaRepository.findByUuid(uuid)?.let {
            ShareUrlMapper.toDomain(it)
        }
    }
}