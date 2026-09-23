package com.placement.controller

import com.placement.dto.*
import com.placement.entity.ApplicationStatus
import com.placement.entity.DriveStatus
import com.placement.service.DriveService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid

// "open" because @Valid is applied by Micronaut AOP, which needs to override these methods
@Controller("/api/drives")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Drives")
open class DriveController(private val driveService: DriveService) {

    // GET /api/drives?status=OPEN
    @Get
    fun getAll(@QueryValue status: DriveStatus?): List<DriveResponse> = driveService.getAll(status)

    @Get("/{id}")
    fun getById(id: Long): DriveResponse = driveService.getById(id)

    @Post
    @Status(HttpStatus.CREATED)
    open fun create(@Valid @Body request: DriveRequest): DriveResponse = driveService.create(request)

    @Put("/{id}/status")
    fun changeStatus(id: Long, @Body request: DriveStatusRequest): DriveResponse =
        driveService.changeStatus(id, request.status)

    // GET /api/drives/1/eligibility?onlyEligible=true
    @Get("/{id}/eligibility")
    fun eligibility(id: Long, @QueryValue(defaultValue = "false") onlyEligible: Boolean): List<EligibilityResponse> =
        driveService.checkEligibility(id, onlyEligible)

    // GET /api/drives/1/applications?status=SHORTLISTED
    @Get("/{id}/applications")
    fun applications(id: Long, @QueryValue status: ApplicationStatus?): List<ApplicationResponse> =
        driveService.getApplications(id, status)

    @Get("/{id}/rounds")
    fun rounds(id: Long): List<RoundResponse> = driveService.getRounds(id)

    @Post("/{id}/rounds")
    @Status(HttpStatus.CREATED)
    open fun addRound(id: Long, @Valid @Body request: RoundRequest): RoundResponse = driveService.addRound(id, request)
}
