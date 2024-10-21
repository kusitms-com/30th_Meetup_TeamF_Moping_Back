package com.ping.infra.nonmember.domain.jpa.repository

import com.ping.infra.nonmember.domain.jpa.entity.NonMemberBookmarkUrlEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NonMemberBookmarkUrlJpaRepository: JpaRepository<NonMemberBookmarkUrlEntity, Long>{
}