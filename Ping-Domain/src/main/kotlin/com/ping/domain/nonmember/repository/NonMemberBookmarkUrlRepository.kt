package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.NonMemberBookmarkUrlDomain

interface NonMemberBookmarkUrlRepository {
    fun saveAll(nonMemberBookmarkUrlDomains: List<NonMemberBookmarkUrlDomain>) : List<NonMemberBookmarkUrlDomain>
    fun findAllByNonMemberId(nonMemberId: Long): List<NonMemberBookmarkUrlDomain>
    fun deleteAllByIds(ids: List<Long>)
}