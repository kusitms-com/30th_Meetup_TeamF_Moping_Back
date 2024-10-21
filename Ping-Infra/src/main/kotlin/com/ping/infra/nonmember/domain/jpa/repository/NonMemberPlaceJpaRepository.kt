package com.ping.infra.nonmember.domain.jpa.repository

import com.ping.infra.nonmember.domain.jpa.entity.NonMemberPlaceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NonMemberPlaceJpaRepository : JpaRepository<NonMemberPlaceEntity, Long> {
}