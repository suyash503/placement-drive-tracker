package com.placement.controller

import com.placement.dto.ApplicationResponse
import com.placement.dto.StudentRequest
import com.placement.dto.StudentResponse
import com.placement.service.ApplicationService
import com.placement.service.StudentService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid

// "open" because @Valid is applied by Micronaut AOP, which needs to override these methods
@Controller("/api/students")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Students")
open class StudentController(
    private val studentService: StudentService,
    private val applicationService: ApplicationService
) {

    // GET /api/students?branch=CSE
    @Get
    fun getAll(@QueryValue branch: String?): List<StudentResponse> = studentService.getAll(branch)

    @Get("/{id}")
    fun getById(id: Long): StudentResponse = studentService.getById(id)

    @Post
    @Status(HttpStatus.CREATED)
    open fun create(@Valid @Body request: StudentRequest): StudentResponse = studentService.create(request)

    @Put("/{id}")
    open fun update(id: Long, @Valid @Body request: StudentRequest): StudentResponse = studentService.update(id, request)

    @Get("/{id}/applications")
    fun getApplications(id: Long): List<ApplicationResponse> = applicationService.getForStudent(id)
}
