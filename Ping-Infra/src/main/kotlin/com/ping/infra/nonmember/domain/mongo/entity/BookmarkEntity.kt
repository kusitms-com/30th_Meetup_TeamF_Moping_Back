package com.ping.infra.nonmember.domain.mongo.entity

import com.ping.infra.nonmember.domain.mongo.common.BaseTimeMongoEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "bookmarks")
data class BookmarkEntity(
    val name: String,
    val px: Double,
    val py: Double,
    @Id
    @Indexed(unique = true)
    val sid: String,
    val address: String,
    val mcidName: String,
    val url: String
): BaseTimeMongoEntity()