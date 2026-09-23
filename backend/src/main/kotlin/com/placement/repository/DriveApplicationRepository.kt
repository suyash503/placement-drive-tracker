package com.placement.repository

import com.placement.entity.ApplicationStatus
import com.placement.entity.DriveApplication
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface DriveApplicationRepository : JpaRepository<DriveApplication, Long> {

    fun findByDriveId(driveId: Long): List<DriveApplication>

    fun findByStudentId(studentId: Long): List<DriveApplication>

    fun existsByStudentIdAndDriveId(studentId: Long, driveId: Long): Boolean

    fun existsByStudentIdAndStatus(studentId: Long, status: ApplicationStatus): Boolean

    fun countByStatus(status: ApplicationStatus): Long

    // A hand-written JPQL query: ids of students who got at least one offer
    @Query("SELECT DISTINCT a.student.id FROM DriveApplication a WHERE a.status = 'SELECTED'")
    fun findPlacedStudentIds(): List<Long>
}
