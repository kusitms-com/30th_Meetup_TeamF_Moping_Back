package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberPlaceDomain

interface NonMemberPlaceRepository {
    fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain>

    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberPlaceDomain>

    fun deleteAll(ids: List<Long>)

    fun findCountBySidIn(sids: List<String>): List<Pair<String, Long>>
}