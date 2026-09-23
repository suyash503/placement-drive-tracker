package com.placement.service

import com.placement.dto.StudentRequest
import com.placement.dto.StudentResponse
import com.placement.entity.Student
import com.placement.exception.BadRequestException
import com.placement.exception.NotFoundException
import com.placement.repository.StudentRepository
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
open class StudentService(
    private val studentRepository: StudentRepository
) {

    // branch is optional: null means "all branches"
    @Transactional
    open fun getAll(branch: String?): List<StudentResponse> {
        val students = if (branch.isNullOrBlank()) {
            studentRepository.findAllOrderByRollNo()
        } else {
            studentRepository.findByBranchOrderByRollNo(branch.uppercase())
        }
        return students.map { StudentResponse.from(it) }
    }

    @Transactional
    open fun getById(id: Long): StudentResponse {
        return StudentResponse.from(findStudent(id))
    }

    @Transactional
    open fun create(request: StudentRequest): StudentResponse {
        if (studentRepository.existsByRollNo(request.rollNo)) {
            throw BadRequestException("Roll number ${request.rollNo} already exists")
        }
        if (studentRepository.existsByEmail(request.email)) {
            throw BadRequestException("Email ${request.email} already exists")
        }
        val student = Student(
            rollNo = request.rollNo.trim().uppercase(),
            name = request.name.trim(),
            email = request.email.trim().lowercase(),
            branch = request.branch.trim().uppercase(),
            cgpa = request.cgpa,
            graduationYear = request.graduationYear,
            activeBacklogs = request.activeBacklogs
        )
        return StudentResponse.from(studentRepository.save(student))
    }

    @Transactional
    open fun update(id: Long, request: StudentRequest): StudentResponse {
        val student = findStudent(id)
        student.name = request.name.trim()
        student.email = request.email.trim().lowercase()
        student.branch = request.branch.trim().uppercase()
        student.cgpa = request.cgpa
        student.graduationYear = request.graduationYear
        student.activeBacklogs = request.activeBacklogs
        return StudentResponse.from(studentRepository.update(student))
    }

    fun findStudent(id: Long): Student {
        return studentRepository.findById(id).orElseThrow { NotFoundException("Student $id not found") }
    }
}
