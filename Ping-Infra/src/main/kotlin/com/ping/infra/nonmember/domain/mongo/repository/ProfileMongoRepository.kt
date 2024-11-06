package com.ping.infra.nonmember.domain.mongo.repository

import com.ping.infra.nonmember.domain.mongo.entity.ProfileEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ProfileMongoRepository : MongoRepository<ProfileEntity, String> {
}