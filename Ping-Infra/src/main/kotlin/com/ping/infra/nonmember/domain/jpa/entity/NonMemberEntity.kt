package com.ping.infra.nonmember.domain.jpa.entity

import com.ping.infra.nonmember.domain.jpa.common.BaseTimeEntity
import jakarta.persistence.*

@Entity(name = "non_member")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "share_url_id"])])
class NonMemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val profileSvg: String,

    @Column(nullable = false)
    val profileLockSvg: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val shareUrl: ShareUrlEntity
) : BaseTimeEntity()