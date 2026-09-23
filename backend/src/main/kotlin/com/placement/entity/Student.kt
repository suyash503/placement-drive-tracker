package com.placement.entity

import jakarta.persistence.*

@Entity
@Table(name = "students")
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "roll_no")
    var rollNo: String = "",

    var name: String = "",

    var email: String = "",

    var branch: String = "",

    var cgpa: Double = 0.0,

    @Column(name = "graduation_year")
    var graduationYear: Int = 0,

    // added later in changeset 002
    @Column(name = "active_backlogs")
    var activeBacklogs: Int = 0
)
