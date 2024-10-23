package com.ping.infra.nonmember.domain.jpa.entity

import com.ping.infra.nonmember.domain.jpa.common.BaseTimeEntity
import jakarta.persistence.*

@Entity(name = "share_url")
class ShareUrlEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val eventName: String,

    @Column(nullable = false)
    val neighborhood: String,

    @Column(nullable = false, unique = true)
    val uuid: String

) : BaseTimeEntity()