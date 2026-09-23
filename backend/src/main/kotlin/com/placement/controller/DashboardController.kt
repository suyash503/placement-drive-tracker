package com.placement.controller

import com.placement.dto.DashboardStats
import com.placement.service.DashboardService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag

@Controller("/api/dashboard")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Dashboard")
class DashboardController(private val dashboardService: DashboardService) {

    @Get("/stats")
    fun stats(): DashboardStats = dashboardService.getStats()
}
