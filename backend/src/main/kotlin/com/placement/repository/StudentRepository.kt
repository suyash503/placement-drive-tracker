package com.placement.repository

import com.placement.entity.Student
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {

    fun findByBranchOrderByRollNo(branch: String): List<Student>

    fun findAllOrderByRollNo(): List<Student>

    fun existsByRollNo(rollNo: String): Boolean

    fun existsByEmail(email: String): Boolean
}
