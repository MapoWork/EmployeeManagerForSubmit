package com.mapo.jjw.em.model

import com.mapo.jjw.em.HOURLY_RATE
import com.mapo.jjw.em.TAX_RATE
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToLong
import kotlin.properties.Delegates

open class Employee {
    private var emNo : UUID
    private var emPart by Delegates.notNull<Int>()
    private var emName by Delegates.notNull<String>()
    private var emDepartment by Delegates.notNull<Department>()
    private var emAge by Delegates.notNull<Int>()
    private var emAddress by Delegates.notNull<String>()
    constructor(emNo : UUID, emPart : Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String) {
        this.emNo = emNo
        this.emPart = emPart
        this.emName = emName
        this.emDepartment = emDepartment
        this.emAge = emAge
        this.emAddress = emAddress
    }
    open fun getEmployeePart() : Int {
        return when(this) {
            is PartTimeEmployee -> 2
            is SalesEmployee -> 1
            is PermanentEmployee, !is SalesEmployee, !is PartTimeEmployee -> 0
            else -> -1
        }
    }
    open fun getEmployeeNo() : UUID {
        return this.emNo
    }
    open fun getEmployeeDepartment() : Department {
        return this.emDepartment
    }
    open fun modifyEmployeeInformation(emName: String?, emDepartment: Department?, emAge: Int?, emAddress: String?) {
        when{
            emName != null && emName != "null" && emName.isNotEmpty() -> { this.emName = emName }
            else -> { }
        }
        when{
            emDepartment != null -> { this.emDepartment = emDepartment }
            else -> { }
        }
        when {
            emAge != null && emAge >= 20 -> { this.emAge = emAge }
            else -> { }
        }
        when {
            emAddress != null && emAddress != "null" && emAddress.isNotEmpty() -> { this.emAddress = emAddress }
            else -> { }
        }
    }
    open fun updateEmployeeInformation(tempEmployee:Employee) {
        when(tempEmployee.emName) {
            "null" -> { }
            else -> this.emName = tempEmployee.emName
        }
        when{
            tempEmployee.emDepartment.equals(null) -> { }
            else -> this.emDepartment = tempEmployee.emDepartment
        }
        when {
            tempEmployee.emAge < 0 -> { }
            else -> this.emAge = tempEmployee.emAge
        }
        when(tempEmployee.emAddress) {
            "null" -> { }
            else -> this.emAddress = tempEmployee.emAddress
        }
    }
    open fun calculateSalary() : Long = 0
    override fun toString() : String {
        val emPartString : String = when(getEmployeePart()) {
            2 -> "파트 타임"
            1 -> "영업 사원"
            0 -> "정규 사원"
            else -> "오류"
        }
        return "[부서 - ${emDepartment.deptCode} : ${emDepartment.deptName}] | [ 구분 - ${emPartString}] | [ 사원 번호 - ${emNo}] | [이름 - ${emName}] | [나이 - ${emAge}] | [주소 - ${emAddress}] | [월급 - ${NumberFormat.getCurrencyInstance(Locale.getDefault()).format(calculateSalary())}]"
    }
}
class PartTimeEmployee : Employee {
    private var emWorkingHour by Delegates.notNull<Long>()
    private var emHourlyRate : Long
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emWorkingHour : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress) {
        this.emWorkingHour = emWorkingHour
        this.emHourlyRate = HOURLY_RATE.toLong()
    }
    fun updatePTEmployeeSalary(tempEmployee:PartTimeEmployee) {
        when {
            tempEmployee.emWorkingHour < 0 -> { }
            else -> this.emWorkingHour = tempEmployee.emWorkingHour
        }
    }
    fun modifyPTEmployeeSalary(emWorkingHour: Long?) {
        when {
            emWorkingHour != null && emWorkingHour > 0 -> { this.emWorkingHour = emWorkingHour }
            else -> { }
        }
    }
    override fun calculateSalary() = emWorkingHour.times(emHourlyRate).times(365).times(TAX_RATE).roundToLong()
}
open class PermanentEmployee : Employee {
    var emSalary by Delegates.notNull<Long>()
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emSalary : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress) {
        this.emSalary = emSalary
    }
    fun updatePEmployeeSalary(tempEmployee:PermanentEmployee) {
        when {
            tempEmployee.emSalary < 0 -> { }
            else -> this.emSalary = tempEmployee.emSalary
        }
    }
    fun modifyPEmployeeSalary(emSalary: Long?) {
        when {
            emSalary != null && emSalary > 0 -> { this.emSalary = emSalary }
            else -> { }
        }
    }
    override fun calculateSalary(): Long = (emSalary.div(12) * (1 - TAX_RATE)).roundToLong()
}
class SalesEmployee : PermanentEmployee {
    private var emSalesPerformance by Delegates.notNull<Long>()
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emSalary : Long, emSalesPerformance : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress, emSalary) {
        this.emSalesPerformance = emSalesPerformance
    }
    fun updateSEmployeeSalary(tempEmployee:SalesEmployee) {
        when {
            tempEmployee.emSalary < 0 -> { }
            else -> this.emSalary = tempEmployee.emSalary
        }
        when {
            tempEmployee.emSalesPerformance < 0 -> { }
            else -> this.emSalesPerformance = tempEmployee.emSalesPerformance
        }
    }
    fun modifySEmployeeSalary(emSalary: Long?, emSalesPerformance: Long?) {
        when {
            emSalary != null && emSalary > 0 -> { this.emSalary = emSalary }
            else -> { }
        }
        when {
            emSalesPerformance != null && emSalesPerformance > 0 -> { this.emSalesPerformance = emSalesPerformance }
            else -> { }
        }
    }
    override fun calculateSalary() = ((emSalary.div(12) + (emSalesPerformance.times(0.05))) * (1 - TAX_RATE)).roundToLong()
}