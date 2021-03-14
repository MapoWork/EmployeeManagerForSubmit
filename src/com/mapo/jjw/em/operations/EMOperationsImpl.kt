package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.Department
import com.mapo.jjw.em.model.Employee
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class EMOperationsImpl : EMOperations {
    private var employeeDataRepo = EmployeeDataRepository()
    override fun getAllEmployee() : List<Employee> {
        return employeeDataRepo.getAllEmployee()
    }
    override fun getEmployeeById(empId: UUID) : Employee {
        val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(empId)
        return employee.get()
    }
    override fun createEmployee(employee: Employee) : Employee? {
        return if (employeeDataRepo.addEmployee(employee)) employee else null
    }
    override fun updateEmployee(employee: Employee) : Employee {
        val employeeFromOperator: Optional<Employee> = employeeDataRepo.getEmployeeById(employee.getEmployeeNo())
        if (employeeFromOperator.isPresent) {
            val employeeValue = employeeFromOperator.get()
            employeeValue.updateEmployeeInformation(employee)
        }
        return employee
    }
    override fun deleteEmployee(empId: UUID) : Boolean {
        when(isValidEmployee(empId)) {
            true -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(empId)
                when (employee.isPresent) {
                    true -> {
                        employeeDataRepo.removeEmployee(employee)
                        return true
                    }
                    false -> return false
                }
            }
            false -> return false
        }
    }
    override fun isValidEmployee(employeeId: UUID) : Boolean {
        return employeeDataRepo.isEmployeeExist(employeeId)
    }
    override fun viewAllEmployee() {
        var salaryTotal : Long = 0
        println("전체 사원 조회 내용은 다음과 같습니다 :\n")
        val counter =  AtomicInteger()
        for (employee in employeeDataRepo.getAllEmployee()) {
            println("사원 ${counter.incrementAndGet()}\n${employee.toString()}")
            salaryTotal += employee.calculateSalary()
        }
        println("\n[전체 사원 수] ${counter.get()}명")
        println("[월 급여 합산 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal)}")
        println("[월 급여 평균 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal.div(counter.get()))}")
    }
    override fun viewDeptEmployee(employeeDepartment: Department) {
        var salaryTotal : Long = 0
        println("[${employeeDepartment.deptName}] 소속 사원 조회 내용은 다음과 같습니다 :\n")
        val counter = AtomicInteger()
        for (employee in employeeDataRepo.getAllEmployee()) {
            when(employee.getEmployeeDepartment().deptName) {
                employeeDepartment.deptName -> {
                    println("사원 ${counter.incrementAndGet()}\n${employee.toString()}")
                    salaryTotal += employee.calculateSalary()
                }
                else -> { }
            }
        }
        println("\n[${employeeDepartment.deptName}] 소속 사원 수] ${counter.get()}명")
        println("[월 급여 합산 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal)}")
        println("[월 급여 평균 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal.div(counter.get()))}")
    }
    override fun viewEmployeeById(id: UUID) {
        when(isValidEmployee(id)) {
            true -> {
                val employee = getEmployeeById(id)
                println("해당 사원 번호 조회 내용은 다음과 같습니다 :\n${employee.toString()}")
            }
            false -> println("사원 번호 $id 조회 실패\n존재하지 않는 사원 번호입니다")
        }
    }
}