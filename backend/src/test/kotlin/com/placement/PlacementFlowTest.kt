package com.placement

import com.placement.dto.*
import com.placement.entity.ApplicationStatus
import com.placement.entity.SlotResult
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * End-to-end test of the whole placement flow through real HTTP calls,
 * against a real MySQL database started by Testcontainers.
 *
 * The tests run in order and share data (company -> drive -> apply -> interview),
 * just like a real placement season.
 */
@MicronautTest(transactional = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PlacementFlowTest {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    // values saved by earlier tests and used by later ones
    companion object {
        var companyId = 0L
        var driveId = 0L
        var goodStudentId = 0L
        var lowCgpaStudentId = 0L
        var applicationId = 0L
        var roundId = 0L
    }

    private fun client() = httpClient.toBlocking()

    // helper: runs a request that we EXPECT to fail and returns the error
    private fun expectError(request: HttpRequest<*>): HttpClientResponseException {
        return assertThrows(HttpClientResponseException::class.java) {
            client().exchange(request, String::class.java)
        }
    }

    @Test
    @Order(1)
    fun createCompany() {
        val response = client().exchange(
            HttpRequest.POST("/api/companies", CompanyRequest("Microsoft", "Technology", null)),
            CompanyResponse::class.java
        )
        assertEquals(HttpStatus.CREATED, response.status)
        companyId = response.body()!!.id

        // same name again should fail
        val error = expectError(HttpRequest.POST("/api/companies", CompanyRequest("Microsoft")))
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    @Test
    @Order(2)
    fun createStudents() {
        val good = StudentRequest("22CS100", "Test Topper", "topper@test.edu", "cse", 9.2, 2026, 0)
        val low = StudentRequest("22CS101", "Test Average", "average@test.edu", "CSE", 6.1, 2026, 0)

        goodStudentId = client().retrieve(HttpRequest.POST("/api/students", good), StudentResponse::class.java).id
        lowCgpaStudentId = client().retrieve(HttpRequest.POST("/api/students", low), StudentResponse::class.java).id

        // branch is saved in upper case
        val saved = client().retrieve(HttpRequest.GET<Any>("/api/students/$goodStudentId"), StudentResponse::class.java)
        assertEquals("CSE", saved.branch)
    }

    @Test
    @Order(3)
    fun invalidStudentIsRejectedByValidation() {
        val badCgpa = StudentRequest("22CS999", "Bad", "bad@test.edu", "CSE", 11.0, 2026, 0)
        val error = expectError(HttpRequest.POST("/api/students", badCgpa))
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    @Test
    @Order(4)
    fun createDrive() {
        val request = DriveRequest(
            companyId = companyId,
            jobRole = "SDE",
            ctcLpa = 45.0,
            driveDate = LocalDate.of(2026, 12, 1),
            minCgpa = 7.5,
            maxBacklogs = 0,
            allowedBranches = listOf("CSE", "IT")
        )
        val drive = client().retrieve(HttpRequest.POST("/api/drives", request), DriveResponse::class.java)
        driveId = drive.id
        assertEquals("Microsoft", drive.companyName)
        assertEquals(listOf("CSE", "IT"), drive.allowedBranches)
    }

    @Test
    @Order(5)
    fun eligibilityListShowsReasons() {
        val list = client().retrieve(
            HttpRequest.GET<Any>("/api/drives/$driveId/eligibility"),
            Array<EligibilityResponse>::class.java
        )
        val good = list.first { it.student.id == goodStudentId }
        val low = list.first { it.student.id == lowCgpaStudentId }
        assertTrue(good.eligible)
        assertFalse(low.eligible)
        assertTrue(low.reasons[0].contains("CGPA"))
    }

    @Test
    @Order(6)
    fun applyToDrive() {
        val application = client().retrieve(
            HttpRequest.POST("/api/applications", ApplyRequest(goodStudentId, driveId)),
            ApplicationResponse::class.java
        )
        applicationId = application.id
        assertEquals(ApplicationStatus.APPLIED, application.status)

        // applying twice is not allowed
        val duplicate = expectError(HttpRequest.POST("/api/applications", ApplyRequest(goodStudentId, driveId)))
        assertEquals(HttpStatus.BAD_REQUEST, duplicate.status)

        // low CGPA student is blocked
        val notEligible = expectError(HttpRequest.POST("/api/applications", ApplyRequest(lowCgpaStudentId, driveId)))
        assertEquals(HttpStatus.BAD_REQUEST, notEligible.status)
        assertTrue(notEligible.response.getBody(String::class.java).get().contains("Not eligible"))
    }

    @Test
    @Order(7)
    fun slotNeedsShortlistedStudent() {
        roundId = client().retrieve(
            HttpRequest.POST("/api/drives/$driveId/rounds", RoundRequest("Technical Interview")),
            RoundResponse::class.java
        ).id

        val start = LocalDateTime.of(2026, 12, 1, 10, 0)
        val error = expectError(
            HttpRequest.POST("/api/rounds/$roundId/slots", SlotRequest(applicationId, start, start.plusHours(1)))
        )
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    @Test
    @Order(8)
    fun shortlistThenBookSlotAndDetectClash() {
        client().exchange(
            HttpRequest.PUT("/api/applications/$applicationId/status", ApplicationStatusRequest(ApplicationStatus.SHORTLISTED)),
            ApplicationResponse::class.java
        )

        val start = LocalDateTime.of(2026, 12, 1, 10, 0)
        val slot = client().retrieve(
            HttpRequest.POST("/api/rounds/$roundId/slots", SlotRequest(applicationId, start, start.plusHours(1))),
            SlotResponse::class.java
        )
        assertEquals(SlotResult.PENDING, slot.result)

        // second round at an overlapping time (10:30) must be rejected
        val round2 = client().retrieve(
            HttpRequest.POST("/api/drives/$driveId/rounds", RoundRequest("HR")),
            RoundResponse::class.java
        )
        assertEquals(2, round2.roundNumber)
        val clash = expectError(
            HttpRequest.POST(
                "/api/rounds/${round2.id}/slots",
                SlotRequest(applicationId, start.plusMinutes(30), start.plusMinutes(90))
            )
        )
        assertTrue(clash.response.getBody(String::class.java).get().contains("Time clash"))
    }

    @Test
    @Order(9)
    fun selectStudentAndCheckDashboard() {
        client().exchange(
            HttpRequest.PUT("/api/applications/$applicationId/status", ApplicationStatusRequest(ApplicationStatus.SELECTED)),
            ApplicationResponse::class.java
        )

        // SELECTED is final - can't go back
        val error = expectError(
            HttpRequest.PUT("/api/applications/$applicationId/status", ApplicationStatusRequest(ApplicationStatus.REJECTED))
        )
        assertEquals(HttpStatus.BAD_REQUEST, error.status)

        val stats = client().retrieve(HttpRequest.GET<Any>("/api/dashboard/stats"), DashboardStats::class.java)
        assertEquals(1, stats.placedStudents)
        assertEquals(2, stats.totalStudents)
        assertEquals(50.0, stats.placementPercentage)
    }

    @Test
    @Order(10)
    fun unknownIdGives404() {
        val error = expectError(HttpRequest.GET<Any>("/api/drives/99999"))
        assertEquals(HttpStatus.NOT_FOUND, error.status)
    }
}
