package com.placement.repository

import com.placement.entity.Round
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface RoundRepository : JpaRepository<Round, Long> {

    fun findByDriveIdOrderByRoundNumber(driveId: Long): List<Round>

    fun existsByDriveIdAndRoundNumber(driveId: Long, roundNumber: Int): Boolean
}
