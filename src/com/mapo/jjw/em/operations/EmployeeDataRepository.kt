package com.mapo.jjw.em.operations

import com.mapo.jjw.em.EmployeeManager.Companion.makeUuid
import com.mapo.jjw.em.model.*
import java.util.*

class EmployeeDataRepository {
    private var employeeDirectory = mutableListOf<Employee>()
    init {
        val employeeRND1 = PartTimeEmployee(makeUuid(),"김파트", Department.GENERAL_AFFAIRS, 20, "서울", 5)
        val employeeRND2 = PartTimeEmployee(makeUuid(),"이파트", Department.RND_LAB, 21, "경기", 6)
        val employeeRND3 = PartTimeEmployee(makeUuid(),"박파트", Department.RND_LAB, 22, "전남", 7)
        val employeeRND4 = PartTimeEmployee(makeUuid(),"강파트", Department.RND_LAB, 23, "충남", 8)
        val employeeRND5 = PartTimeEmployee(makeUuid(),"고파트", Department.RND_LAB, 24, "강원", 9)
        val employeeMT1 = PermanentEmployee(makeUuid(),"나정규", Department.GENERAL_AFFAIRS, 25, "서울", 30000000)
        val employeeMT2 = PermanentEmployee(makeUuid(),"윤정규", Department.MARKETING_TEAM, 26, "경기", 35000000)
        val employeeMT3 = PermanentEmployee(makeUuid(),"안정규", Department.MARKETING_TEAM, 27, "경북", 40000000)
        val employeeMT4 = PermanentEmployee(makeUuid(),"석정규", Department.MARKETING_TEAM, 28, "제주", 45000000)
        val employeeMT5 = PermanentEmployee(makeUuid(),"유정규", Department.MARKETING_TEAM, 29, "강원", 50000000)
        val employeeSE1 = SalesEmployee(makeUuid(),"유영업", Department.GENERAL_AFFAIRS, 30, "인천", 30000000, 20000000)
        val employeeSE2 = SalesEmployee(makeUuid(),"양영업", Department.CS_TEAM, 31, "경기", 35000000, 25000000)
        val employeeSE3 = SalesEmployee(makeUuid(),"우영업", Department.CS_TEAM, 32, "경남", 40000000, 30000000)
        val employeeSE4 = SalesEmployee(makeUuid(),"송영업", Department.CS_TEAM, 33, "충북", 45000000, 35000000)
        val employeeSE5 = SalesEmployee(makeUuid(),"권영업", Department.CS_TEAM, 34, "전북", 50000000, 40000000)
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
    fun set(employeeDir: MutableList<Employee>) {
        employeeDirectory = employeeDir
    }
    fun addEmployee(employee: Employee) : Boolean{
        return this.employeeDirectory.add(employee)
    }
    fun removeEmployee(employee: Optional<Employee>) {
        this.employeeDirectory.remove(employee.get())
    }
    fun getEmployeeById(employeeNo: UUID): Optional<Employee> {
        return this.employeeDirectory.parallelStream().filter { employee -> employee.getEmployeeNo() == employeeNo }.findAny()
    }
    fun getAllEmployee(): MutableList<Employee> {
        return this.employeeDirectory
    }
    fun isEmployeeExist(employeeNo: UUID): Boolean {
        return this.employeeDirectory.parallelStream().anyMatch { employee -> employee.getEmployeeNo() == employeeNo }
    }
}