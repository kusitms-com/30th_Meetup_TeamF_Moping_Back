package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberUpdateStatusDomain

interface NonMemberUpdateStatusRepository {
    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberUpdateStatusDomain>
    fun saveAll(statuses: List<NonMemberUpdateStatusDomain>): List<NonMemberUpdateStatusDomain>
}