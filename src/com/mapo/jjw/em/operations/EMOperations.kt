package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.Department
import com.mapo.jjw.em.model.Employee
import java.util.*

interface EMOperations {
    fun getAllEmployee() : List<Employee>
    fun getEmployeeById(employeeId: UUID) : Employee
    fun createEmployee(employee: Employee) : Employee?
    fun updateEmployee(employee: Employee) : Employee
    fun deleteEmployee(employeeId: UUID) : Boolean
    fun isValidEmployee(employeeId: UUID) : Boolean
    fun viewAllEmployee()
    fun viewDeptEmployee(employeeDepartment: Department)
    fun viewEmployeeById(id: UUID)
}