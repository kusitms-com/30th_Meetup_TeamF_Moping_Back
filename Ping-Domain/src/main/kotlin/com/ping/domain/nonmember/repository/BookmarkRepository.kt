package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.BookmarkDomain

interface BookmarkRepository {
    fun saveAll(bookmarkDomains: List<BookmarkDomain>) : List<BookmarkDomain>

    fun findAllBySidIn(sids: List<String>) : List<BookmarkDomain>
    fun findByLocationNear(px: Double, py: Double, distance: Double): List<BookmarkDomain>
}