package com.ping.infra.nonmember.domain.jpa.repository

import com.ping.infra.nonmember.domain.jpa.entity.RecommendPlaceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendPlaceJpaRepository : JpaRepository<RecommendPlaceEntity, Long> {
}