package com.ping.infra.nonmember.domain.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "places")
data class PlaceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double
)