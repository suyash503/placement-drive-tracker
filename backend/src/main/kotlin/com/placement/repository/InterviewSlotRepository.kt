package com.placement.repository

import com.placement.entity.InterviewSlot
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface InterviewSlotRepository : JpaRepository<InterviewSlot, Long> {

    fun findByRoundIdOrderByStartTime(roundId: Long): List<InterviewSlot>

    // all slots of one student, across every drive (used to detect clashes)
    fun findByApplicationStudentId(studentId: Long): List<InterviewSlot>

    fun existsByRoundIdAndApplicationId(roundId: Long, applicationId: Long): Boolean
}
