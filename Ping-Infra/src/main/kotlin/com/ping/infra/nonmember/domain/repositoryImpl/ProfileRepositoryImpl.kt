package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.ProfileDomain
import com.ping.domain.nonmember.repository.ProfileRepository
import com.ping.infra.nonmember.domain.mapper.ProfileMapper
import com.ping.infra.nonmember.domain.mongo.repository.ProfileMongoRepository
import org.springframework.stereotype.Repository

@Repository
class ProfileRepositoryImpl(
    private val profileMongoRepository: ProfileMongoRepository
) : ProfileRepository {
    override fun findAll(): List<ProfileDomain> {
        return profileMongoRepository.findAll().map { ProfileMapper.toDomain(it) }
    }
}