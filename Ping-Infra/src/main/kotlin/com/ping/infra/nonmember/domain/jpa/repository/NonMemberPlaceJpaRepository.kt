package com.ping.infra.nonmember.domain.jpa.repository

import com.ping.infra.nonmember.domain.jpa.entity.NonMemberPlaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NonMemberPlaceJpaRepository : JpaRepository<NonMemberPlaceEntity, Long> {
    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberPlaceEntity>

    @Query("SELECT p.sid AS sid, COUNT(p.sid) AS count FROM non_member_place p WHERE p.sid IN :sids GROUP BY p.sid")
    fun findCountBySidIn(sids: List<String>): List<Pair<String, Long>>
}