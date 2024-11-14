package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberPlaceDomain
import com.ping.domain.nonmember.dto.SidCount

interface NonMemberPlaceRepository {
    fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain>

    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberPlaceDomain>

    fun deleteAll(ids: List<Long>)

    fun findCountBySidIn(sids: List<String>): List<SidCount>

    fun findAllByNonMemberIdIn(nonMemberIds: List<Long>): List<NonMemberPlaceDomain>
}