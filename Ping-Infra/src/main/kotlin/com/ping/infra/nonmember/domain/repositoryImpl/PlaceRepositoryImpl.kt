package com.ping.infra.nonmember.domain.repositoryImpl

import com.ping.domain.nonmember.aggregate.PlaceDomain
import com.ping.domain.nonmember.repository.PlaceRepository
import org.springframework.stereotype.Repository

@Repository
class PlaceRepositoryImpl(
    private val placeJpaRepository: PlaceJpaRepository
) : PlaceRepository {
    override fun save(place: PlaceDomain) {
        val placeEntity = PlaceEntity(
            name = place.name,
            address = place.address,
            latitude = place.latitude,
            longitude = place.longitude
        )
        placeJpaRepository.save(placeEntity)
    }
}