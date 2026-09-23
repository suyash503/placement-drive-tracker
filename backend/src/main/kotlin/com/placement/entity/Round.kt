package com.placement.entity

import jakarta.persistence.*

// One interview round of a drive, e.g. "Round 1 - Online Assessment"
@Entity
@Table(name = "rounds")
class Round(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "drive_id")
    var drive: Drive? = null,

    @Column(name = "round_number")
    var roundNumber: Int = 1,

    var name: String = ""
)
