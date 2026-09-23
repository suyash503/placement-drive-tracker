package com.placement.repository

import com.placement.entity.InterviewSlot
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface InterviewSlotRepository : JpaRepository<InterviewSlot, Long> {

    fun findByRoundIdOrderByStartTime(roundId: Long): List<InterviewSlot>

    // All slots of one student, across every drive (used to detect time clashes).
    // Written as JPQL because the path goes two levels deep (slot -> application -> student),
    // and the method-name version "findByApplicationStudentId" was not resolved correctly.
    @Query("SELECT s FROM InterviewSlot s WHERE s.application.student.id = :studentId")
    fun findAllForStudent(studentId: Long): List<InterviewSlot>

    fun existsByRoundIdAndApplicationId(roundId: Long, applicationId: Long): Boolean
}
