package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.BookmarkDomain
import com.ping.domain.nonmember.repository.BookmarkRepository
import com.ping.infra.nonmember.domain.mapper.BookmarkMapper
import com.ping.infra.nonmember.domain.mongo.repository.BookmarkMongoRepository
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepositoryImpl(
    private val bookmarkMongoRepository: BookmarkMongoRepository
) : BookmarkRepository {
    override fun saveAll(bookmarkDomains: List<BookmarkDomain>): List<BookmarkDomain> {
        return bookmarkMongoRepository.saveAll(bookmarkDomains.map { BookmarkMapper.toEntity(it) }).map {
            BookmarkMapper.toDomain(it)
        }
    }

    override fun findAllBySidIn(sids: List<String>): List<BookmarkDomain> {
        return bookmarkMongoRepository.findAllBySidIn(sids).map { BookmarkMapper.toDomain(it) }
    }

}