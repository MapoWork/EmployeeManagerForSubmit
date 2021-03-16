package com.mapo.jjw.em.model

import com.mapo.jjw.em.HOURLY_RATE
import com.mapo.jjw.em.TAX_RATE
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToLong
import kotlin.properties.Delegates

/**
 * Implements an API for Employee class
 * Employee 클래스는 모든 사원 객체의 클래스가 공통적으로 상속받는 클래스 -> 영업 사원 클래스의 경우, 정규 사원 클래스를 상속받음
 * 공통으로 가지는 멤버변수에 대한 조회, 등록, 변경 절차를 Employee 클래스를 통해 업캐스팅되어 처리
 * @author Jungwoo Jo
 */
open class Employee {
    private var emNo : UUID
    private var emPart by Delegates.notNull<Int>()              // 변경이 잦을 수 있는 멤버 변수는
    private var emName by Delegates.notNull<String>()           // getter, setter 구현이 아닌
    private var emDepartment by Delegates.notNull<Department>() // Property Delegate 방식으로 선언하여
    private var emAge by Delegates.notNull<Int>()               // 늦은 초기화가 가능하도록 하였음
    private var emAddress by Delegates.notNull<String>()        //   정확한 목적, 관련성에 대해 공부 필요성...
    /**
     * 생성자
     * 사원 번호, 사원 구분, 이름, 부서, 나이, 주소를 파라미터로 받아 멤버변수로 할당
     */
    constructor(emNo : UUID, emPart : Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String) {
        this.emNo = emNo
        this.emPart = emPart
        this.emName = emName
        this.emDepartment = emDepartment
        this.emAge = emAge
        this.emAddress = emAddress
    }
    /**
     * 사원 구분을 Int 형식으로 반환해주는 메서드 -> 정규 사원은 0, 영업 사원은 1, 파트 타임 사원은 2
     */
    open fun getEmployeePart() : Int {
        return when(this) {
            is PartTimeEmployee -> 2
            is SalesEmployee -> 1
            is PermanentEmployee, !is SalesEmployee, !is PartTimeEmployee -> 0
            else -> -1
        }
    }
    /**
     * 해당 사원 객체의 사원 번호를 반환해주는 getter
     */
    open fun getEmployeeNo() : UUID {
        return this.emNo
    }
    /**
     * 해당 사원 객체의 부서를 반환해주는 getter
     */
    open fun getEmployeeDepartment() : Department {
        return this.emDepartment
    }
    /**
     * 해당 사원 객체의 기본 정보(이름, 부서, 나이, 주소)를 수정해주는 setter
     */
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
    /**
     * 사원의 기본 정보를 변경하기 위해 임시로 변경 정보를 저장한 사원 객체를 파라미터로 받고
     * 해당 객체의 변경된 기본 정보를 저장소 내 실제 사원 객체로 업데이트해주는 setter
     */
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
    /**
     * 사원의 월급을 계산해주는 메서드 -> 프로토타입
     */
    open fun calculateSalary() : Long = 0
    /**
     * 해당 사원 객체의 부서, 사원 구분, 사원 번호, 이름, 나이, 주소, 월급 정보를 반환해주는 오버라이딩 메서드
     * 해당 사원 객체 이름을 출력 시 toString 메서드의 반환 값이 출력
     */
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

/**
 * Implements an API for PartTimeEmployee class
 * PartTimeEmployee 클래스는 파트 타임 사원을 명시하는 클래스로 Employee 클래스를 상속받아 구현
 * 월급 정보 변경을 위한 메서드가 존재하며, 월급 정보 출력은 calculateSalary 메서드를 오버라이딩하여 구현
 * @author Jungwoo Jo
 */
class PartTimeEmployee : Employee {
    private var emWorkingHour by Delegates.notNull<Long>() // Property Delegate
    private var emHourlyRate : Long // EmployeeManager 클래스의 최상단에 선언한 상수 값을 그대로 가짐
    /**
     * 생성자
     * 기본 정보 이외에 일일 근무시간, 시급 정보도 파라미터로 받아 할당
     */
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emWorkingHour : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress) {
        this.emWorkingHour = emWorkingHour
        this.emHourlyRate = HOURLY_RATE.toLong()
    }
    /**
     * 해당 사원 객체의 연봉 정보 변경 사항을 담은 사원 객체를 파라미터로 받아 저장소의 실제 사원 객체로 업데이트해주는 메서드
     */
    fun updatePTEmployeeSalary(tempEmployee:PartTimeEmployee) {
        when {
            tempEmployee.emWorkingHour < 0 -> { }
            else -> this.emWorkingHour = tempEmployee.emWorkingHour
        }
    }
    /**
     * 해당 임시 월급 정보 변경 사항을 담을 사원 객체의 연봉 정보를 변경해주는 메서드
     */
    fun modifyPTEmployeeSalary(emWorkingHour: Long?) {
        when {
            emWorkingHour != null && emWorkingHour > 0 -> { this.emWorkingHour = emWorkingHour }
            else -> { }
        }
    }
    /**
     * 월급을 계산해주는 오버라이딩 메서드
     */
    override fun calculateSalary() = emWorkingHour.times(emHourlyRate).times(365).times(TAX_RATE).roundToLong()
}

/**
 * Implements an API for PermanentEmployee class
 * PartTimeEmployee 클래스는 정규 사원을 명시하는 클래스로 Employee 클래스를 상속받아 구현
 * 월급 정보 변경을 위한 메서드가 존재하며, 월급 정보 출력은 calculateSalary 메서드를 오버라이딩하여 구현
 * @author Jungwoo Jo
 */
open class PermanentEmployee : Employee {
    var emSalary by Delegates.notNull<Long>() // Property Delegate
    /**
     * 생성자
     * 기본 정보 이외에 연봉 정보도 파라미터로 받아 할당
     */
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emSalary : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress) {
        this.emSalary = emSalary
    }
    /**
     * 해당 사원 객체의 연봉 정보 변경 사항을 담은 사원 객체를 파라미터로 받아 저장소의 실제 사원 객체로 업데이트해주는 메서드
     */
    fun updatePEmployeeSalary(tempEmployee:PermanentEmployee) {
        when {
            tempEmployee.emSalary < 0 -> { }
            else -> this.emSalary = tempEmployee.emSalary
        }
    }
    /**
     * 해당 임시 월급 정보 변경 사항을 담을 사원 객체의 연봉 정보를 변경해주는 메서드
     */
    fun modifyPEmployeeSalary(emSalary: Long?) {
        when {
            emSalary != null && emSalary > 0 -> { this.emSalary = emSalary }
            else -> { }
        }
    }
    /**
     * 월급을 계산해주는 오버라이딩 메서드
     */
    override fun calculateSalary(): Long = (emSalary.div(12) * (1 - TAX_RATE)).roundToLong()
}

/**
 * Implements an API for SalesEmployee class
 * SalesEmployee 클래스는 영업 사원을 명시하는 클래스로 PermanentEmployee 클래스를 상속받아 구현
 * 월급 정보 변경을 위한 메서드가 존재하며, 월급 정보 출력은 calculateSalary 메서드를 오버라이딩하여 구현
 * @author Jungwoo Jo
 */
class SalesEmployee : PermanentEmployee {
    private var emSalesPerformance by Delegates.notNull<Long>() // Property Delegate
    /**
     * 생성자
     * 기본 정보 이외에 연봉 정보와 연간 영업 실적 정보도 파라미터로 받아 할당
     */
    constructor(emNo : UUID, emPart: Int, emName : String, emDepartment : Department, emAge : Int, emAddress : String, emSalary : Long, emSalesPerformance : Long) : super(emNo, emPart, emName, emDepartment, emAge, emAddress, emSalary) {
        this.emSalesPerformance = emSalesPerformance
    }
    /**
     * 해당 사원 객체의 연봉 정보 변경 사항을 담은 사원 객체를 파라미터로 받아 저장소의 실제 사원 객체로 업데이트해주는 메서드
     */
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
    /**
     * 해당 임시 월급 정보 변경 사항을 담을 사원 객체의 연봉 정보를 변경해주는 메서드
     */
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
    /**
     * 월급을 계산해주는 오버라이딩 메서드
     */
    override fun calculateSalary() = ((emSalary.div(12) + (emSalesPerformance.times(0.05))) * (1 - TAX_RATE)).roundToLong()
}