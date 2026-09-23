package com.placement.entity

import jakarta.persistence.*
import java.time.LocalDateTime

// A time slot given to one student (application) for one round
@Entity
@Table(name = "interview_slots")
class InterviewSlot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "round_id")
    var round: Round? = null,

    @ManyToOne
    @JoinColumn(name = "application_id")
    var application: DriveApplication? = null,

    @Column(name = "start_time")
    var startTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "end_time")
    var endTime: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var result: SlotResult = SlotResult.PENDING
)
