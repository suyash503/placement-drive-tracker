package com.placement.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "drives")
class Drive(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // many drives can belong to one company
    @ManyToOne
    @JoinColumn(name = "company_id")
    var company: Company? = null,

    @Column(name = "job_role")
    var jobRole: String = "",

    @Column(name = "ctc_lpa")
    var ctcLpa: Double = 0.0,

    @Column(name = "drive_date")
    var driveDate: LocalDate = LocalDate.now(),

    // ---- eligibility rules ----
    @Column(name = "min_cgpa")
    var minCgpa: Double = 0.0,

    @Column(name = "max_backlogs")
    var maxBacklogs: Int = 0,

    // stored in a separate table "drive_branches" (drive_id, branch)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "drive_branches", joinColumns = [JoinColumn(name = "drive_id")])
    @Column(name = "branch")
    var allowedBranches: MutableSet<String> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    var status: DriveStatus = DriveStatus.OPEN
)
