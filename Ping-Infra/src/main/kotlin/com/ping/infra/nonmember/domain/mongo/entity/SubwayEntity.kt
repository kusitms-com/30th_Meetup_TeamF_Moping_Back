package com.ping.infra.nonmember.domain.mongo.entity

import com.ping.infra.nonmember.domain.mongo.common.BaseTimeMongoEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "subway")
data class SubwayEntity(
    @Id
    @Indexed(unique = true)
    val id: String,
    val station: String
): BaseTimeMongoEntity()