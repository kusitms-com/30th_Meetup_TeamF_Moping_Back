package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.SubwayDomain

interface SubwayRepository {
    fun findByStation(station: String) : SubwayDomain?
}