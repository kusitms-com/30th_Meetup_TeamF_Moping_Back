package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberPlaceDomain

interface NonMemberPlaceRepository {
    fun saveAll(nonMemberPlaceDomains: List<NonMemberPlaceDomain>): List<NonMemberPlaceDomain>
}