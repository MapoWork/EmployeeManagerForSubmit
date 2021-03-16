package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.Department
import com.mapo.jjw.em.model.Employee
import java.util.*

/**
 * Implements an API for EMOperations interface
 * EMOperations 인터페이스는 EMOperationsImplement 클래스를 통해 구현
 * @author Jungwoo Jo
 */
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