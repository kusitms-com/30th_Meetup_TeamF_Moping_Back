package com.ping.infra.nonmember.domain.jpa.repository

import com.ping.infra.nonmember.domain.jpa.entity.NonMemberStoreUrlEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NonMemberStoreUrlJpaRepository : JpaRepository<NonMemberStoreUrlEntity, Long> {
}