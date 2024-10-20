package com.ping.domain.nonmember.repository

import com.ping.domain.nonmember.aggregate.ShareUrlDomain

interface ShareUrlRepository {
    fun findByUuid(uuid: String): ShareUrlDomain?
}
