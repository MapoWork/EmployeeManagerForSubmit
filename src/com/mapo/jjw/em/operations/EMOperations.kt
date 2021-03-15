package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.Department
import com.mapo.jjw.em.model.Employee
import java.util.*

interface EMOperations {
    fun getAllEmployee() : List<Employee>
    fun getDepartmentEmployee(department: Department) : List<Employee>
    fun getEmployeeById(employeeId: UUID, employeePart: Int) : Employee
    fun createEmployee(employee: Employee) : Employee?
    fun updateEmployee(employee: Employee) : Employee
    fun deleteEmployee(employeeId: UUID) : Boolean
    fun isValidEmployee(employeeId: UUID) : Int
    fun viewAllEmployee()
    fun viewDeptEmployee(department: Department)
    fun viewEmployeeById(employeeId: UUID)
}