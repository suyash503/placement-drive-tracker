package com.placement.dto

import com.placement.entity.*
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalDateTime

// These classes describe the JSON we send back.
// We never return entities directly - that way the API shape doesn't change
// every time a database column changes, and we avoid lazy-loading surprises.

@Serdeable
data class CompanyResponse(
    val id: Long,
    val name: String,
    val industry: String?,
    val website: String?
) {
    companion object {
        fun from(company: Company) = CompanyResponse(
            id = company.id!!,
            name = company.name,
            industry = company.industry,
            website = company.website
        )
    }
}

@Serdeable
data class StudentResponse(
    val id: Long,
    val rollNo: String,
    val name: String,
    val email: String,
    val branch: String,
    val cgpa: Double,
    val graduationYear: Int,
    val activeBacklogs: Int
) {
    companion object {
        fun from(student: Student) = StudentResponse(
            id = student.id!!,
            rollNo = student.rollNo,
            name = student.name,
            email = student.email,
            branch = student.branch,
            cgpa = student.cgpa,
            graduationYear = student.graduationYear,
            activeBacklogs = student.activeBacklogs
        )
    }
}

@Serdeable
data class DriveResponse(
    val id: Long,
    val companyId: Long,
    val companyName: String,
    val jobRole: String,
    val ctcLpa: Double,
    val driveDate: LocalDate,
    val minCgpa: Double,
    val maxBacklogs: Int,
    val allowedBranches: List<String>,
    val status: DriveStatus
) {
    companion object {
        fun from(drive: Drive) = DriveResponse(
            id = drive.id!!,
            companyId = drive.company!!.id!!,
            companyName = drive.company!!.name,
            jobRole = drive.jobRole,
            ctcLpa = drive.ctcLpa,
            driveDate = drive.driveDate,
            minCgpa = drive.minCgpa,
            maxBacklogs = drive.maxBacklogs,
            allowedBranches = drive.allowedBranches.sorted(),
            status = drive.status
        )
    }
}

@Serdeable
data class ApplicationResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val rollNo: String,
    val branch: String,
    val cgpa: Double,
    val driveId: Long,
    val companyName: String,
    val jobRole: String,
    val status: ApplicationStatus,
    val appliedAt: LocalDateTime
) {
    companion object {
        fun from(application: DriveApplication): ApplicationResponse {
            val student = application.student!!
            val drive = application.drive!!
            return ApplicationResponse(
                id = application.id!!,
                studentId = student.id!!,
                studentName = student.name,
                rollNo = student.rollNo,
                branch = student.branch,
                cgpa = student.cgpa,
                driveId = drive.id!!,
                companyName = drive.company!!.name,
                jobRole = drive.jobRole,
                status = application.status,
                appliedAt = application.appliedAt
            )
        }
    }
}

@Serdeable
data class RoundResponse(
    val id: Long,
    val driveId: Long,
    val roundNumber: Int,
    val name: String
) {
    companion object {
        fun from(round: Round) = RoundResponse(
            id = round.id!!,
            driveId = round.drive!!.id!!,
            roundNumber = round.roundNumber,
            name = round.name
        )
    }
}

@Serdeable
data class SlotResponse(
    val id: Long,
    val roundId: Long,
    val roundName: String,
    val applicationId: Long,
    val studentName: String,
    val rollNo: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val result: SlotResult
) {
    companion object {
        fun from(slot: InterviewSlot) = SlotResponse(
            id = slot.id!!,
            roundId = slot.round!!.id!!,
            roundName = slot.round!!.name,
            applicationId = slot.application!!.id!!,
            studentName = slot.application!!.student!!.name,
            rollNo = slot.application!!.student!!.rollNo,
            startTime = slot.startTime,
            endTime = slot.endTime,
            result = slot.result
        )
    }
}

// Used by the "check eligibility" screen: every student + whether they can apply and why not
@Serdeable
data class EligibilityResponse(
    val student: StudentResponse,
    val eligible: Boolean,
    val alreadyApplied: Boolean,
    val reasons: List<String>
)

@Serdeable
data class BranchStats(
    val branch: String,
    val totalStudents: Int,
    val placedStudents: Int
)

@Serdeable
data class DashboardStats(
    val totalCompanies: Long,
    val totalStudents: Long,
    val openDrives: Long,
    val totalApplications: Long,
    val placedStudents: Int,
    val placementPercentage: Double,
    val applicationsByStatus: Map<String, Long>,
    val branchWise: List<BranchStats>
)

@Serdeable
data class ErrorResponse(
    val status: Int,
    val message: String
)
