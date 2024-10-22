package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.PlaceDomain

interface PlaceRepository {
    fun save(place: PlaceDomain)
}