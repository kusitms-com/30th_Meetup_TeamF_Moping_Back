package com.ping.infra.nonmember.domain.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface NonMemberUpdateStatusJpaRepository : JpaRepository<NonMemberUpdateStatusEntity, Long> {
    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberUpdateStatusEntity>
}