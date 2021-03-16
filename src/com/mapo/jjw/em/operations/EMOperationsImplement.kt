package com.mapo.jjw.em.operations

import com.mapo.jjw.em.model.*
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implements an API for EMOperationsImplement class
 * EMOperationsImplement 클래스는 EMOperations 인터페이스를 상속받아 각 메서드를 오버라이딩하여 구현
 * EmployerManager 클래스는 이 클래스를 통해 사원 객체 저장소에 접근하고 제어
 * @author Jungwoo Jo
 */
class EMOperationsImplement : EMOperations {
    private var employeeDataRepo = EmployeeDataRepository() // 접근하고 제어할 사원 객체 저장소 객체를 멤버 변수로 가짐
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 모든 사원의 객체가 담긴 List 컬렉션을 반환
     */
    override fun getAllEmployee() : List<Employee> {
        return employeeDataRepo.getAllEmployee()
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 사원 중
     * 파라미터로 받은 사원 구분과 일치하는 모든 사원의 객체가 담긴 List 컬렉션을 반환
     */
    override fun getDepartmentEmployee(department: Department): List<Employee> {
        return employeeDataRepo.getDepartmentEmployee(department)
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 사원 중
     * 파라미터로 받은 사원 번호, 사원 구분과 일치하는 사원의 객체를 반환
     * 해당 사원이 존재하지 않을 시, 빈 Employee 객체 반환
     * NPE 오류 등 null 체크를 위해 해당 과정을 Optional 클래스로 위임하였으나,
     * isValidEmployee 메서드를 통해 존재 여부를 체크한 이후 실행하므로 로직 상 발생 불가능
     */
    override fun getEmployeeById(employeeId : UUID, employeePart : Int) : Employee {
        val employee: Optional<Employee> = employeeDataRepo.getEmployeeById(employeeId, employeePart)
        return employee.get()
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 파라미터로 받은 사원 객체를 추가
     */
    override fun createEmployee(employee: Employee) : Employee? {
        return if (employeeDataRepo.addEmployee(employee)) employee else null
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 파라미터로 받은 사원 변경 정보가 적용된 임시 사원 객체를 업데이트
     * 모든 사원 클래스가 포함하는 기본 정보는 한 메서드로 처리하지만, 월급 정보 변경은 사원 클래스마다 멤버 변수가 다른 이유료
     * 사원 구분에 따라 분기하여 각 사원 클래스에서 월급 정보 업데이트를 담당하는 메서드에서 각기 다르게 처리
     */
    override fun updateEmployee(employee: Employee) : Employee {
        val employeeFromOperator: Optional<Employee> = employeeDataRepo.getEmployeeById(employee.getEmployeeNo(), employee.getEmployeePart())
        if (employeeFromOperator.isPresent) { // NSEE 오류 방지를 위한 체크
            val employeeValue = employeeFromOperator.get()
            employeeValue.updateEmployeeInformation(employee)
            when(employeeValue.getEmployeePart()) {
                0 -> {
                    (employeeValue as PermanentEmployee).updatePEmployeeSalary(employeeValue)
                }
                1 -> {
                    (employeeValue as SalesEmployee).updateSEmployeeSalary(employeeValue)
                }
                2 -> {
                    (employeeValue as PartTimeEmployee).updatePTEmployeeSalary(employeeValue)
                }
            }
        }
        return employee
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 파라미터로 받은 사원 번호와 일치하는 사원 객체를 제거
     */
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
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 파라미터로 받은 사원 번호를 가지는 객체가 존재하는지 검사하고
     * 만일 존재할 시, 각 사원 구분 값을 Int 형식으로 반환 -> 0은 정규 사원, 1은 영업 사원, 2는 파트 타임 사원
     */
    override fun isValidEmployee(employeeId: UUID) : Int {
        return employeeDataRepo.getEmployeeExistInformation(employeeId)
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 모든 사원 컬렉션 객체를
     * 저장소 클래스의 getAllEmployee 메서드를 통해 받고 컬렉션 내에 포함된 모든 사원 객체의 정보를
     * Employee 클래스에서 오버라이딩한 toString 메서드를 통해 출력 -> 객체 이름을 출력 시 자동으로 toString 출력
     * 모든 사원의 수, 월급 정보 합산 금액, 평균 금액을 계산하여 함께 출력
     */
    override fun viewAllEmployee() {
        var salaryTotal : Long = 0
        println("전체 사원 조회 내용은 다음과 같습니다 :\n")
        val counter =  AtomicInteger()
        for (employee in employeeDataRepo.getAllEmployee()) {
            println("사원 ${counter.incrementAndGet()}\n$employee")
            salaryTotal += employee.calculateSalary()
        }
        println("\n[전체 사원 수] ${counter.get()}명")
        println("[월 급여 합산 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal)}")
        println("[월 급여 평균 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal.div(counter.get()))}")
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 모든 사원 컬렉션 객체를
     * 저장소 클래스의 getAllEmployee 메서드를 통해 받고 컬렉션 내에 포함된 모든 사원 객체 중
     * 파라미터로 받은 부서와 일치하는 사원 객체의 정보만 필터링하여
     * Employee 클래스에서 오버라이딩한 toString 메서드를 통해 출력 -> 객체 이름을 출력 시 자동으로 toString 출력
     * 해당되는 모든 사원의 수, 월급 정보 합산 금액, 평균 금액을 계산하여 함께 출력
     */
    override fun viewDeptEmployee(department: Department) {
        var salaryTotal : Long = 0
        println("[${department.deptName}] 소속 사원 조회 내용은 다음과 같습니다 :\n")
        val counter = AtomicInteger()
        for (employee in employeeDataRepo.getAllEmployee()) {
            when(employee.getEmployeeDepartment().deptName) {
                department.deptName -> {
                    println("사원 ${counter.incrementAndGet()}\n$employee")
                    salaryTotal += employee.calculateSalary()
                }
                else -> { }
            }
        }
        println("\n[${department.deptName}] 소속 사원 수] ${counter.get()}명")
        println("[월 급여 합산 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal)}")
        println("[월 급여 평균 금액] ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(salaryTotal.div(counter.get()))}")
    }
    /**
     * EmployeeDataRepository 저장소 MutableList 컬렉션에 저장된 사원 객체 중
     * 파라미터로 받은 사원 번호와 일치하는 사원 객체만을 필터링하여
     * 해당되는 사원 객체의 정보를 Employee 클래스에서 오버라이딩한 toString 메서드를 통해 출력 -> 객체 이름을 출력 시 자동으로 toString 출력
     */
    override fun viewEmployeeById(employeeId: UUID) {
        when(isValidEmployee(employeeId)) {
            in 0..2 -> {
                val employee = getEmployeeById(employeeId, isValidEmployee(employeeId))
                println("해당 사원 번호 조회 내용은 다음과 같습니다 :\n$employee")
            }
            else -> println("사원 번호 $employeeId 조회 실패\n존재하지 않는 사원 번호입니다")
        }
    }
}