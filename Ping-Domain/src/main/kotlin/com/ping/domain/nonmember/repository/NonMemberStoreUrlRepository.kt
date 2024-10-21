package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberStoreUrlDomain

interface NonMemberStoreUrlRepository {
    fun saveALl(nonMemberStoreUrlDomains: List<NonMemberStoreUrlDomain>) : List<NonMemberStoreUrlDomain>
}