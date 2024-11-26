package com.ping.domain.ping.repository

import com.ping.domain.ping.aggregate.NonMemberPlaceDomain
import com.ping.domain.ping.dto.SidCount

interface NonMemberPlaceRepository {
    fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain>

    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberPlaceDomain>

    fun deleteAll(ids: List<Long>)

    fun findCountBySidIn(sids: List<String>): List<SidCount>

    fun findAllByNonMemberIdIn(nonMemberIds: List<Long>): List<NonMemberPlaceDomain>
}