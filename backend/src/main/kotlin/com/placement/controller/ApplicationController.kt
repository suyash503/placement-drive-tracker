package com.placement.controller

import com.placement.dto.ApplicationResponse
import com.placement.dto.ApplicationStatusRequest
import com.placement.dto.ApplyRequest
import com.placement.service.ApplicationService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag

@Controller("/api/applications")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Applications")
class ApplicationController(private val applicationService: ApplicationService) {

    @Post
    @Status(HttpStatus.CREATED)
    fun apply(@Body request: ApplyRequest): ApplicationResponse = applicationService.apply(request)

    @Put("/{id}/status")
    fun changeStatus(id: Long, @Body request: ApplicationStatusRequest): ApplicationResponse =
        applicationService.changeStatus(id, request.status)
}
