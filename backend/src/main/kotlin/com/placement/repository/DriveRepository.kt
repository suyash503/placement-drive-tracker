package com.placement.repository

import com.placement.entity.Drive
import com.placement.entity.DriveStatus
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface DriveRepository : JpaRepository<Drive, Long> {

    fun findByStatusOrderByDriveDate(status: DriveStatus): List<Drive>

    fun findAllOrderByDriveDate(): List<Drive>

    // "CompanyId" means drive.company.id
    fun findByCompanyId(companyId: Long): List<Drive>

    fun existsByCompanyId(companyId: Long): Boolean

    fun countByStatus(status: DriveStatus): Long
}
