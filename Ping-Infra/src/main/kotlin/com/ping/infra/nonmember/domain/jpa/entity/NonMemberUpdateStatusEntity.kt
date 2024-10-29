package com.ping.infra.nonmember.domain.jpa.entity

import com.ping.infra.nonmember.domain.jpa.common.BaseTimeEntity
import jakarta.persistence.*


@Entity
@Table(name = "non_member_update_status")
data class NonMemberUpdateStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "non_member_id", nullable = false)
    val nonMember: NonMemberEntity,

    @Column(nullable = false)
    val friendId: Long,

    @Column(nullable = false)
    var isUpdate: Boolean = false
) : BaseTimeEntity()