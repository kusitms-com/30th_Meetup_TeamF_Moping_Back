package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberDomain

interface NonMemberRepository {
    fun findByShareUrlIdAndName(shareUrlId: Long, name: String): NonMemberDomain?

    fun save(nonMemberDomain: NonMemberDomain): NonMemberDomain

    fun findAllByShareUrl(shareUrlId: Long): List<NonMemberDomain>

    fun findById(nonMemberId: Long): NonMemberDomain?

    fun findAllBySidAndShareUrlId(sid: String, sharedUrlId: Long): List<NonMemberDomain>
}