package com.ping.infra.nonmember.domain.mongo.repository

import com.ping.infra.nonmember.domain.mongo.entity.SubwayEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface SubwayMongoRepository : MongoRepository<SubwayEntity, String> {
    fun findByStation(station: String): SubwayEntity?
}