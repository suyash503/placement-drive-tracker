package com.placement.repository

import com.placement.entity.Company
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

// Micronaut Data writes the SQL for these methods at COMPILE time, just from the method names
@Repository
interface CompanyRepository : JpaRepository<Company, Long> {

    fun existsByName(name: String): Boolean

    fun findAllOrderByName(): List<Company>
}
