package com.pingping.share.domain

import com.pingping.global.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "non_member")
class NonMember(
    @Column(nullable = false)
    val name: String,

    val password: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_url_id", nullable = false)
    var shareUrl: ShareUrl? = null
) : BaseTimeEntity()