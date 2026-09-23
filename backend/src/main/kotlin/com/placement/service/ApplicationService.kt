package com.placement.service

import com.placement.dto.ApplicationResponse
import com.placement.dto.ApplyRequest
import com.placement.eligibility.EligibilityChecker
import com.placement.entity.ApplicationStatus
import com.placement.entity.DriveApplication
import com.placement.entity.DriveStatus
import com.placement.exception.BadRequestException
import com.placement.exception.NotFoundException
import com.placement.repository.DriveApplicationRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class ApplicationService(
    private val applicationRepository: DriveApplicationRepository,
    private val studentService: StudentService,
    private val driveService: DriveService
) {

    @Transactional
    open fun getForStudent(studentId: Long): List<ApplicationResponse> {
        studentService.findStudent(studentId)
        return applicationRepository.findByStudentId(studentId)
            .sortedByDescending { it.appliedAt }
            .map { ApplicationResponse.from(it) }
    }

    /**
     * A student applies to a drive. We check, in order:
     * 1. the drive is still OPEN
     * 2. the student hasn't already applied
     * 3. the student meets the eligibility rules
     */
    @Transactional
    open fun apply(request: ApplyRequest): ApplicationResponse {
        val student = studentService.findStudent(request.studentId)
        val drive = driveService.findDrive(request.driveId)

        if (drive.status != DriveStatus.OPEN) {
            throw BadRequestException("Applications for this drive are closed")
        }

        if (applicationRepository.existsByStudentIdAndDriveId(student.id!!, drive.id!!)) {
            throw BadRequestException("${student.name} has already applied to this drive")
        }

        val alreadyPlaced = applicationRepository.existsByStudentIdAndStatus(student.id!!, ApplicationStatus.SELECTED)
        val problems = EligibilityChecker.findProblems(
            student.cgpa,
            student.branch,
            student.activeBacklogs,
            alreadyPlaced,
            drive.minCgpa,
            drive.allowedBranches,
            drive.maxBacklogs
        )
        if (problems.isNotEmpty()) {
            throw BadRequestException("Not eligible: " + problems.joinToString("; "))
        }

        val application = DriveApplication(student = student, drive = drive)
        return ApplicationResponse.from(applicationRepository.save(application))
    }

    /**
     * Moves an application forward. Allowed moves:
     *   APPLIED     -> SHORTLISTED or REJECTED
     *   SHORTLISTED -> SELECTED or REJECTED
     * SELECTED and REJECTED are final.
     */
    @Transactional
    open fun changeStatus(id: Long, newStatus: ApplicationStatus): ApplicationResponse {
        val application = findApplication(id)
        val current = application.status

        val allowed = when (current) {
            ApplicationStatus.APPLIED -> listOf(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED)
            ApplicationStatus.SHORTLISTED -> listOf(ApplicationStatus.SELECTED, ApplicationStatus.REJECTED)
            ApplicationStatus.SELECTED -> emptyList()
            ApplicationStatus.REJECTED -> emptyList()
        }

        if (newStatus !in allowed) {
            throw BadRequestException("Cannot change status from $current to $newStatus")
        }

        application.status = newStatus
        return ApplicationResponse.from(applicationRepository.update(application))
    }

    fun findApplication(id: Long): DriveApplication {
        return applicationRepository.findById(id).orElseThrow { NotFoundException("Application $id not found") }
    }
}
