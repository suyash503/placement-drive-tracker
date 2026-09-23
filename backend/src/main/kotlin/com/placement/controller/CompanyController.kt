package com.placement.controller

import com.placement.dto.CompanyRequest
import com.placement.dto.CompanyResponse
import com.placement.service.CompanyService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid

// @ExecuteOn(BLOCKING): database calls block the thread, so we run them on a worker
// thread pool instead of the Netty event loop (which should never be blocked).
// "open" because @Valid is applied by Micronaut AOP, which needs to override these methods
@Controller("/api/companies")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Companies")
open class CompanyController(private val companyService: CompanyService) {

    @Get
    fun getAll(): List<CompanyResponse> = companyService.getAll()

    @Get("/{id}")
    fun getById(id: Long): CompanyResponse = companyService.getById(id)

    @Post
    @Status(HttpStatus.CREATED)
    open fun create(@Valid @Body request: CompanyRequest): CompanyResponse = companyService.create(request)

    @Put("/{id}")
    open fun update(id: Long, @Valid @Body request: CompanyRequest): CompanyResponse = companyService.update(id, request)

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(id: Long) = companyService.delete(id)
}
