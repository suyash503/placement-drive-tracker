package com.placement.controller

import com.placement.dto.SlotRequest
import com.placement.dto.SlotResponse
import com.placement.dto.SlotResultRequest
import com.placement.service.InterviewService
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag

@Controller("/api")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "Interviews")
class InterviewController(private val interviewService: InterviewService) {

    @Get("/rounds/{roundId}/slots")
    fun getSlots(roundId: Long): List<SlotResponse> = interviewService.getSlots(roundId)

    @Post("/rounds/{roundId}/slots")
    @Status(HttpStatus.CREATED)
    fun bookSlot(roundId: Long, @Body request: SlotRequest): SlotResponse = interviewService.bookSlot(roundId, request)

    @Put("/slots/{slotId}/result")
    fun recordResult(slotId: Long, @Body request: SlotResultRequest): SlotResponse =
        interviewService.recordResult(slotId, request.result)
}
