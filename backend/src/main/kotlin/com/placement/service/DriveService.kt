package com.placement.service

import com.placement.dto.*
import com.placement.eligibility.EligibilityChecker
import com.placement.entity.ApplicationStatus
import com.placement.entity.Drive
import com.placement.entity.DriveStatus
import com.placement.entity.Round
import com.placement.exception.BadRequestException
import com.placement.exception.NotFoundException
import com.placement.repository.DriveApplicationRepository
import com.placement.repository.DriveRepository
import com.placement.repository.RoundRepository
import com.placement.repository.StudentRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class DriveService(
    private val driveRepository: DriveRepository,
    private val studentRepository: StudentRepository,
    private val applicationRepository: DriveApplicationRepository,
    private val roundRepository: RoundRepository,
    private val companyService: CompanyService
) {

    @Transactional
    open fun getAll(status: DriveStatus?): List<DriveResponse> {
        val drives = if (status == null) {
            driveRepository.findAllOrderByDriveDate()
        } else {
            driveRepository.findByStatusOrderByDriveDate(status)
        }
        return drives.map { DriveResponse.from(it) }
    }

    @Transactional
    open fun getById(id: Long): DriveResponse {
        return DriveResponse.from(findDrive(id))
    }

    @Transactional
    open fun create(request: DriveRequest): DriveResponse {
        val company = companyService.findCompany(request.companyId)

        val drive = Drive(
            company = company,
            jobRole = request.jobRole.trim(),
            ctcLpa = request.ctcLpa,
            driveDate = request.driveDate,
            minCgpa = request.minCgpa,
            maxBacklogs = request.maxBacklogs,
            allowedBranches = request.allowedBranches.map { it.trim().uppercase() }.toMutableSet(),
            status = DriveStatus.OPEN
        )
        return DriveResponse.from(driveRepository.save(drive))
    }

    @Transactional
    open fun changeStatus(id: Long, newStatus: DriveStatus): DriveResponse {
        val drive = findDrive(id)
        if (drive.status == DriveStatus.COMPLETED) {
            throw BadRequestException("Drive is already completed")
        }
        drive.status = newStatus
        return DriveResponse.from(driveRepository.update(drive))
    }

    /**
     * Goes through every student and tells whether they can apply to this drive.
     * The actual rules live in the Java class EligibilityChecker.
     */
    @Transactional
    open fun checkEligibility(driveId: Long, onlyEligible: Boolean): List<EligibilityResponse> {
        val drive = findDrive(driveId)
        val placedStudentIds = applicationRepository.findPlacedStudentIds().toSet()

        // ids of students who already applied to this drive
        val appliedStudentIds = mutableSetOf<Long>()
        for (application in applicationRepository.findByDriveId(driveId)) {
            appliedStudentIds.add(application.student!!.id!!)
        }

        val result = mutableListOf<EligibilityResponse>()
        for (student in studentRepository.findAllOrderByRollNo()) {
            val reasons = EligibilityChecker.findProblems(
                student.cgpa,
                student.branch,
                student.activeBacklogs,
                placedStudentIds.contains(student.id),
                drive.minCgpa,
                drive.allowedBranches,
                drive.maxBacklogs
            )
            val eligible = reasons.isEmpty()
            if (onlyEligible && !eligible) {
                continue
            }
            result.add(
                EligibilityResponse(
                    student = StudentResponse.from(student),
                    eligible = eligible,
                    alreadyApplied = appliedStudentIds.contains(student.id),
                    reasons = reasons
                )
            )
        }
        return result
    }

    // ---------- Rounds ----------

    @Transactional
    open fun getRounds(driveId: Long): List<RoundResponse> {
        findDrive(driveId) // throws 404 if the drive doesn't exist
        return roundRepository.findByDriveIdOrderByRoundNumber(driveId).map { RoundResponse.from(it) }
    }

    @Transactional
    open fun addRound(driveId: Long, request: RoundRequest): RoundResponse {
        val drive = findDrive(driveId)
        if (drive.status == DriveStatus.COMPLETED) {
            throw BadRequestException("Cannot add rounds to a completed drive")
        }
        // next round number = number of existing rounds + 1
        val existingRounds = roundRepository.findByDriveIdOrderByRoundNumber(driveId)
        val round = Round(
            drive = drive,
            roundNumber = existingRounds.size + 1,
            name = request.name.trim()
        )
        return RoundResponse.from(roundRepository.save(round))
    }

    // ---------- Applications of a drive ----------

    @Transactional
    open fun getApplications(driveId: Long, status: ApplicationStatus?): List<ApplicationResponse> {
        findDrive(driveId)
        var applications = applicationRepository.findByDriveId(driveId)
        if (status != null) {
            applications = applications.filter { it.status == status }
        }
        return applications.sortedBy { it.student!!.rollNo }.map { ApplicationResponse.from(it) }
    }

    fun findDrive(id: Long): Drive {
        return driveRepository.findById(id).orElseThrow { NotFoundException("Drive $id not found") }
    }
}
