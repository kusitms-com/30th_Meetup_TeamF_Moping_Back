package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberStoreUrlDomain

interface NonMemberStoreUrlRepository {
    fun saveAll(nonMemberStoreUrlDomains: List<NonMemberStoreUrlDomain>) : List<NonMemberStoreUrlDomain>
}