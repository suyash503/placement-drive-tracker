package com.placement.dto

import com.placement.entity.ApplicationStatus
import com.placement.entity.DriveStatus
import com.placement.entity.SlotResult
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.time.LocalDateTime

// These classes describe the JSON the frontend sends to us.
// The validation annotations are checked automatically because controllers use @Valid.

@Serdeable
data class CompanyRequest(
    @field:NotBlank val name: String,
    val industry: String? = null,
    val website: String? = null
)

@Serdeable
data class StudentRequest(
    @field:NotBlank val rollNo: String,
    @field:NotBlank val name: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank val branch: String,
    @field:DecimalMin("0.0") @field:DecimalMax("10.0") val cgpa: Double,
    @field:Min(2000) val graduationYear: Int,
    @field:Min(0) val activeBacklogs: Int = 0
)

@Serdeable
data class DriveRequest(
    val companyId: Long,
    @field:NotBlank val jobRole: String,
    @field:Positive val ctcLpa: Double,
    val driveDate: LocalDate,
    @field:DecimalMin("0.0") @field:DecimalMax("10.0") val minCgpa: Double,
    @field:Min(0) val maxBacklogs: Int = 0,
    val allowedBranches: List<String> = emptyList()
)

@Serdeable
data class DriveStatusRequest(
    val status: DriveStatus
)

@Serdeable
data class ApplyRequest(
    val studentId: Long,
    val driveId: Long
)

@Serdeable
data class ApplicationStatusRequest(
    val status: ApplicationStatus
)

@Serdeable
data class RoundRequest(
    @field:NotBlank val name: String
)

@Serdeable
data class SlotRequest(
    val applicationId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

@Serdeable
data class SlotResultRequest(
    val result: SlotResult
)
