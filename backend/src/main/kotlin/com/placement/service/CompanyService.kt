package com.placement.service

import com.placement.dto.CompanyRequest
import com.placement.dto.CompanyResponse
import com.placement.entity.Company
import com.placement.exception.BadRequestException
import com.placement.exception.NotFoundException
import com.placement.repository.CompanyRepository
import com.placement.repository.DriveRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class CompanyService(
    private val companyRepository: CompanyRepository,
    private val driveRepository: DriveRepository
) {

    @Transactional
    open fun getAll(): List<CompanyResponse> {
        return companyRepository.findAllOrderByName().map { CompanyResponse.from(it) }
    }

    @Transactional
    open fun getById(id: Long): CompanyResponse {
        return CompanyResponse.from(findCompany(id))
    }

    @Transactional
    open fun create(request: CompanyRequest): CompanyResponse {
        if (companyRepository.existsByName(request.name.trim())) {
            throw BadRequestException("Company '${request.name}' already exists")
        }
        val company = Company(
            name = request.name.trim(),
            industry = request.industry,
            website = request.website
        )
        return CompanyResponse.from(companyRepository.save(company))
    }

    @Transactional
    open fun update(id: Long, request: CompanyRequest): CompanyResponse {
        val company = findCompany(id)
        company.name = request.name.trim()
        company.industry = request.industry
        company.website = request.website
        return CompanyResponse.from(companyRepository.update(company))
    }

    @Transactional
    open fun delete(id: Long) {
        val company = findCompany(id)
        if (driveRepository.existsByCompanyId(id)) {
            throw BadRequestException("Cannot delete ${company.name}: it has placement drives")
        }
        companyRepository.delete(company)
    }

    fun findCompany(id: Long): Company {
        return companyRepository.findById(id).orElseThrow { NotFoundException("Company $id not found") }
    }
}
