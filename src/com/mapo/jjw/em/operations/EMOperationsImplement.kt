package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.*
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class EMOperationsImplement : EMOperations {
    private var employeeDataRepo = EmployeeDataRepository()
    override fun getAllEmployee() : List<Employee> {
        return employeeDataRepo.getAllEmployee()
    }

    override fun getDepartmentEmployee(department: Department): List<Employee> {
        return employeeDataRepo.getDepartmentEmployee(department)
    }
    override fun getEmployeeById(employeeId : UUID, employeePart : Int) : Employee {
        when (employeePart) {
            0 -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, employeePart)
                return employee.get()
            }
            1 -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, employeePart)
                return employee.get()
            }
            2 -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, employeePart)
                return employee.get()
            }
            else -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, -1)
                return employee.get()
            }
        }
    }
    override fun createEmployee(employee: Employee) : Employee? {
        return if (employeeDataRepo.addEmployee(employee)) employee else null
    }
    override fun updateEmployee(employee: Employee) : Employee {
        val employeeFromOperator: Optional<Employee> = employeeDataRepo.getEmployeeById(employee.getEmployeeNo(), employee.getEmployeePart())
        if (employeeFromOperator.isPresent) {
            val employeeValue = employeeFromOperator.get()
            employeeValue.updateEmployeeInformation(employee)
            when(employeeValue.getEmployeePart()) {
                0 -> {
                    (employeeValue as PermanentEmployee).updateEmployeeSalary(employeeValue)
                }
                1 -> {
                    (employeeValue as SalesEmployee).updateEmployeeInformation(employeeValue)
                }
                2 -> {
                    (employeeValue as PartTimeEmployee).updateEmployeeInformation(employeeValue)
                }
            }
        }
        return employee
    }
    override fun deleteEmployee(employeeId: UUID) : Boolean {
        return when(isValidEmployee(employeeId)) {
            in 0..2 -> {
                val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, isValidEmployee(employeeId))
                when (employee.isPresent) {
                    true -> {
                        employeeDataRepo.removeEmployee(employee)
                        true
                    }
                    false -> false
                }
            }
            else -> false
        }
    }
    override fun isValidEmployee(employeeId: UUID) : Int {
        return employeeDataRepo.getEmployeeExistInformation(employeeId)
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
    override fun viewDeptEmployee(department: Department) {
        var salaryTotal : Long = 0
        println("[${department.deptName}] 소속 사원 조회 내용은 다음과 같습니다 :\n")
        val counter = AtomicInteger()
        for (employee in employeeDataRepo.getAllEmployee()) {
            when(employee.getEmployeeDepartment().deptName) {
                department.deptName -> {
                    println("사원 ${counter.incrementAndGet()}\n${employee.toString()}")
                    salaryTotal += employee.calculateSalary()
                }
                else -> { }
            }
        }
        println("\n[${department.deptName}] 소속 사원 수] ${counter.get()}명")
        println("[월 급여 합산 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal)}")
        println("[월 급여 평균 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal.div(counter.get()))}")
    }
    override fun viewEmployeeById(employeeId: UUID) {
        when(isValidEmployee(employeeId)) {
            in 0..2 -> {
                val employee = getEmployeeById(employeeId, isValidEmployee(employeeId))
                println("해당 사원 번호 조회 내용은 다음과 같습니다 :\n${employee.toString()}")
            }
            else -> println("사원 번호 $employeeId 조회 실패\n존재하지 않는 사원 번호입니다")
        }
    }
}