package com.placement.entity

import jakarta.persistence.*
import java.time.LocalDateTime

// Named DriveApplication (not Application) so it doesn't get confused with the app's main class
@Entity
@Table(name = "applications")
class DriveApplication(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "student_id")
    var student: Student? = null,

    @ManyToOne
    @JoinColumn(name = "drive_id")
    var drive: Drive? = null,

    @Enumerated(EnumType.STRING)
    var status: ApplicationStatus = ApplicationStatus.APPLIED,

    @Column(name = "applied_at")
    var appliedAt: LocalDateTime = LocalDateTime.now()
)
