package com.mapo.jjw.em.operations

import com.mapo.jjw.em.EmployeeManager.Companion.makeUuid
import com.mapo.jjw.em.model.*
import java.util.*

/**
 * Implements an API for EmployeeDataRepository class
 * EmployeeDataRepository 클래스는 사원 정보를 MutableList 컬렉션으로 보관
 * 조회, 등록, 변경 과정을 수행하는 저장소 역할, 이를테면 Database
 * @author Jungwoo Jo
 */
class EmployeeDataRepository {
    private var employeeDirectory = mutableListOf<Employee>() // 저장소 MutableList 컬렉션
    /**
     * 초기화
     * 저장소 MutableList 컬렉션에 사원 구분 별로 각각 5명의 사원을 임의로 추가
     */
    init {
        val employeeRND1 = PartTimeEmployee(makeUuid(), 2, "김파트", Department.GENERAL_AFFAIRS, 20, "서울", 5)
        val employeeRND2 = PartTimeEmployee(makeUuid(), 2, "이파트", Department.RND_LAB, 21, "경기", 6)
        val employeeRND3 = PartTimeEmployee(makeUuid(), 2, "박파트", Department.RND_LAB, 22, "전남", 7)
        val employeeRND4 = PartTimeEmployee(makeUuid(), 2, "강파트", Department.RND_LAB, 23, "충남", 8)
        val employeeRND5 = PartTimeEmployee(makeUuid(), 2, "고파트", Department.RND_LAB, 24, "강원", 9)
        val employeeMT1 = PermanentEmployee(makeUuid(), 0, "나정규", Department.GENERAL_AFFAIRS, 25, "서울", 30000000)
        val employeeMT2 = PermanentEmployee(makeUuid(), 0, "윤정규", Department.MARKETING_TEAM, 26, "경기", 35000000)
        val employeeMT3 = PermanentEmployee(makeUuid(), 0, "안정규", Department.MARKETING_TEAM, 27, "경북", 40000000)
        val employeeMT4 = PermanentEmployee(makeUuid(), 0, "석정규", Department.MARKETING_TEAM, 28, "제주", 45000000)
        val employeeMT5 = PermanentEmployee(makeUuid(), 0, "유정규", Department.MARKETING_TEAM, 29, "강원", 50000000)
        val employeeSE1 = SalesEmployee(makeUuid(), 1, "유영업", Department.GENERAL_AFFAIRS, 30, "인천", 30000000, 20000000)
        val employeeSE2 = SalesEmployee(makeUuid(), 1, "양영업", Department.CS_TEAM, 31, "경기", 35000000, 25000000)
        val employeeSE3 = SalesEmployee(makeUuid(), 1, "우영업", Department.CS_TEAM, 32, "경남", 40000000, 30000000)
        val employeeSE4 = SalesEmployee(makeUuid(), 1, "송영업", Department.CS_TEAM, 33, "충북", 45000000, 35000000)
        val employeeSE5 = SalesEmployee(makeUuid(), 1, "권영업", Department.CS_TEAM, 34, "전북", 50000000, 40000000)
        employeeDirectory.add(employeeRND1)
        employeeDirectory.add(employeeRND2)
        employeeDirectory.add(employeeRND3)
        employeeDirectory.add(employeeRND4)
        employeeDirectory.add(employeeRND5)
        employeeDirectory.add(employeeMT1)
        employeeDirectory.add(employeeMT2)
        employeeDirectory.add(employeeMT3)
        employeeDirectory.add(employeeMT4)
        employeeDirectory.add(employeeMT5)
        employeeDirectory.add(employeeSE1)
        employeeDirectory.add(employeeSE2)
        employeeDirectory.add(employeeSE3)
        employeeDirectory.add(employeeSE4)
        employeeDirectory.add(employeeSE5)
    }
    /**
     * 저장소 MutableList 컬렉션에 사원 객체를 추가하는 메서드
     */
    fun addEmployee(employee: Employee) : Boolean{
        return this.employeeDirectory.add(employee)
    }
    /**
     * 저장소 MutableList 컬렉션에 저장된 사원 객체를 제거하는 메서드
     */
    fun removeEmployee(employee: Optional<Employee>) {
        this.employeeDirectory.remove(employee.get())
    }
    /**
     * 저장소 MutableList 컬렉션에 저장된 사원 중 매개변수로 받은 사원 번호와 사원 구분이 일치하는 사원 객체를 반환해주는 메서드
     */
    fun getEmployeeById(employeeNo : UUID, employeePart : Int): Optional<Employee> {
        return this.employeeDirectory.parallelStream().filter { employee -> employee.getEmployeeNo() == employeeNo }.filter{
            employee -> employee.getEmployeePart() == employeePart
        }.findAny()
    }
    /**
     * 저장소 MutableList 컬렉션에 저장된 모든 사원을 조회하기 위해 컬렉션 객체 자체를 반환해주는 메서드
     */
    fun getAllEmployee(): MutableList<Employee> {
        return employeeDirectory
    }
    /**
     * 저장소 MutableList 컬렉션에 저장된 사원 중 매개변수로 받은 사원 구분과 일치하는 사원들을 병렬 스트림을 통해 필터링
     * 필터링 후 정렬하여 각각 임시 MutableList 컬렉션에 저장하여 그 객체를 반환해주는 메서드
     */
    fun getDepartmentEmployee(employeeDepartment: Department): MutableList<Employee> {
        val employeeDepartmentDirectory = mutableListOf<Employee>()
        this.employeeDirectory.parallelStream().filter { employee -> employee.getEmployeeDepartment() == employeeDepartment }.sorted().forEach(employeeDepartmentDirectory::add)
        return employeeDepartmentDirectory
    }
    /**
     * 저장소 MutableList 컬렉션 내에 매개변수로 받은 사원 번호에 해당하는 사원 객체가 존재하는 지 확인하고
     * 해당 사원이 존재할 시 사원 구분 값을 반환해주는 메서드
     * 객체가 존재하지 않을 시, -1을 반환
     */
    fun getEmployeeExistInformation(employeeNo: UUID) : Int {
        return when(this.employeeDirectory.parallelStream().anyMatch { employee -> employee.getEmployeeNo() == employeeNo }) {
            true -> {
                this.employeeDirectory.parallelStream().filter { employee -> employee.getEmployeeNo() == employeeNo }.findAny().get().getEmployeePart()
            }
            false -> -1
        }
    }
}