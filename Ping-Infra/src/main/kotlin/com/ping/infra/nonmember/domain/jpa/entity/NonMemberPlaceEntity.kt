package com.ping.infra.nonmember.domain.jpa.entity

import com.ping.infra.nonmember.domain.jpa.common.BaseTimeEntity
import jakarta.persistence.*

@Entity(name = "non_member_place")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["non_member_id", "sid"])])
class NonMemberPlaceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val nonMember: NonMemberEntity,

    @Column(nullable = false)
    val sid: String
) : BaseTimeEntity()