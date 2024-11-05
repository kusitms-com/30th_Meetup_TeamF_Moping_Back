package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.ProfileDomain

interface ProfileRepository {
    fun findAll(): List<ProfileDomain>
}