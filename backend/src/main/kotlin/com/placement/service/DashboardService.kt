package com.placement.service

import com.placement.dto.BranchStats
import com.placement.dto.DashboardStats
import com.placement.entity.ApplicationStatus
import com.placement.entity.DriveStatus
import com.placement.repository.CompanyRepository
import com.placement.repository.DriveApplicationRepository
import com.placement.repository.DriveRepository
import com.placement.repository.StudentRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class DashboardService(
    private val companyRepository: CompanyRepository,
    private val studentRepository: StudentRepository,
    private val driveRepository: DriveRepository,
    private val applicationRepository: DriveApplicationRepository
) {

    @Transactional
    open fun getStats(): DashboardStats {
        val students = studentRepository.findAll()
        val placedIds = applicationRepository.findPlacedStudentIds().toSet()

        // count of applications in each status, e.g. {"APPLIED": 4, "SELECTED": 1, ...}
        val byStatus = mutableMapOf<String, Long>()
        for (status in ApplicationStatus.entries) {
            byStatus[status.name] = applicationRepository.countByStatus(status)
        }

        // group students by branch and count how many of them are placed
        val branchWise = mutableListOf<BranchStats>()
        val studentsByBranch = students.groupBy { it.branch }
        for ((branch, branchStudents) in studentsByBranch.toSortedMap()) {
            val placed = branchStudents.count { placedIds.contains(it.id) }
            branchWise.add(BranchStats(branch, branchStudents.size, placed))
        }

        var percentage = 0.0
        if (students.isNotEmpty()) {
            percentage = placedIds.size * 100.0 / students.size
            percentage = Math.round(percentage * 10) / 10.0 // keep one decimal place
        }

        return DashboardStats(
            totalCompanies = companyRepository.count(),
            totalStudents = students.size.toLong(),
            openDrives = driveRepository.countByStatus(DriveStatus.OPEN),
            totalApplications = applicationRepository.count(),
            placedStudents = placedIds.size,
            placementPercentage = percentage,
            applicationsByStatus = byStatus,
            branchWise = branchWise
        )
    }
}
