package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.RecommendPlaceDomain

interface RecommendPlaceRepository {
    fun saveAll(recommendPlaceDomains: List<RecommendPlaceDomain>): List<RecommendPlaceDomain>

    fun findAllByShareUrlId(shareUrlId: Long): List<RecommendPlaceDomain>
}