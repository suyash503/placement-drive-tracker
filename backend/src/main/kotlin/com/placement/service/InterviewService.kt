package com.placement.service

import com.placement.dto.SlotRequest
import com.placement.dto.SlotResponse
import com.placement.entity.ApplicationStatus
import com.placement.entity.InterviewSlot
import com.placement.entity.Round
import com.placement.entity.SlotResult
import com.placement.exception.BadRequestException
import com.placement.exception.NotFoundException
import com.placement.repository.DriveApplicationRepository
import com.placement.repository.InterviewSlotRepository
import com.placement.repository.RoundRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class InterviewService(
    private val roundRepository: RoundRepository,
    private val slotRepository: InterviewSlotRepository,
    private val applicationRepository: DriveApplicationRepository,
    private val applicationService: ApplicationService
) {

    @Transactional
    open fun getSlots(roundId: Long): List<SlotResponse> {
        findRound(roundId)
        return slotRepository.findByRoundIdOrderByStartTime(roundId).map { SlotResponse.from(it) }
    }

    /**
     * Books an interview slot for a shortlisted student.
     * The same student can't have two interviews at the same time (even for different companies).
     */
    @Transactional
    open fun bookSlot(roundId: Long, request: SlotRequest): SlotResponse {
        val round = findRound(roundId)
        val application = applicationService.findApplication(request.applicationId)

        if (application.drive!!.id != round.drive!!.id) {
            throw BadRequestException("This application belongs to a different drive")
        }
        if (application.status != ApplicationStatus.SHORTLISTED) {
            throw BadRequestException("Only shortlisted students can get interview slots")
        }
        if (!request.endTime.isAfter(request.startTime)) {
            throw BadRequestException("End time must be after start time")
        }
        if (slotRepository.existsByRoundIdAndApplicationId(roundId, application.id!!)) {
            throw BadRequestException("Student already has a slot in this round")
        }

        // check every other slot of this student for a time clash
        val studentId = application.student!!.id!!
        for (other in slotRepository.findByApplicationStudentId(studentId)) {
            // two time ranges overlap when each one starts before the other ends
            val overlaps = request.startTime.isBefore(other.endTime) && other.startTime.isBefore(request.endTime)
            if (overlaps) {
                val company = other.application!!.drive!!.company!!.name
                throw BadRequestException(
                    "Time clash: student already has '${other.round!!.name}' with $company " +
                        "from ${other.startTime} to ${other.endTime}"
                )
            }
        }

        val slot = InterviewSlot(
            round = round,
            application = application,
            startTime = request.startTime,
            endTime = request.endTime
        )
        return SlotResponse.from(slotRepository.save(slot))
    }

    /**
     * Records PASSED / FAILED for a slot.
     * If the student fails a round, their application is automatically rejected.
     */
    @Transactional
    open fun recordResult(slotId: Long, result: SlotResult): SlotResponse {
        val slot = slotRepository.findById(slotId).orElseThrow { NotFoundException("Slot $slotId not found") }
        slot.result = result

        if (result == SlotResult.FAILED) {
            val application = slot.application!!
            if (application.status == ApplicationStatus.SHORTLISTED) {
                application.status = ApplicationStatus.REJECTED
                applicationRepository.update(application)
            }
        }

        return SlotResponse.from(slotRepository.update(slot))
    }

    private fun findRound(id: Long): Round {
        return roundRepository.findById(id).orElseThrow { NotFoundException("Round $id not found") }
    }
}
