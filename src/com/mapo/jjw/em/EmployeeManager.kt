package com.mapo.jjw.em

import com.mapo.jjw.em.model.*
import com.mapo.jjw.em.model.Department.*
import com.mapo.jjw.em.operations.EMOperationsImplement
import java.lang.Exception
import java.util.*
import kotlin.system.exitProcess
import java.util.UUID
import kotlin.collections.ArrayList

const val TAX_RATE = 0.08F     // 세율 - 월급 계산 시 일괄 적용
const val HOURLY_RATE = 25000  // 시급 - PartTimeEmployee 객체의 월급 계산 시 사용

/**
 * Implements an API for EmployeeManger class
 * EmployeeManager 클래스는 프로그램 전체 로직을 담당
 * @author Jungwoo Jo
 */
class EmployeeManager {
    private val emScanner = Scanner(System.`in`)
    private val emOperations = EMOperationsImplement() // EMOperationsImplement
                                                       // 클래스의 객체를 멤버변수르 가짐

    /**
     * UUID 클래스를 상속받는 makeUuid 함수를 동반 객체 내부로 정의 -> UUID 자체로도 static ...?
     * UUID.randomUUID() 값을 구분자 '-'로 파싱한 뒤
     * String 배열로 다시 생성하여 사원번호를 할당해주는 함수
     * (JAVA 자체적인 split 버그로 해줘야 하는 작업)
     */
    companion object {
        fun makeUuid():UUID {
            val uuidString:String = UUID.randomUUID().toString()
            val parts = arrayOf(
                uuidString.substring(0, 7),
                uuidString.substring(9, 12),
                uuidString.substring(14, 17),
                uuidString.substring(19, 22),
                uuidString.substring(24, 35)
            )
            val m1 = parts[0].toLong(16)
            val m2 = parts[1].toLong(16)
            val m3 = parts[2].toLong(16)
            val lsb1 = parts[3].toLong(16)
            val lsb2 = parts[4].toLong(16)
            val msb = m1 shl 32 or (m2 shl 16) or m3
            val lsb = lsb1 shl 48 or lsb2
            return UUID(msb, lsb)
        }
    }

    /**
     * 메인 함수로부터 사용자 커맨드를 파라미터로 받아
     * 1부터 7까지의 각 작업을 when 제어문으로 분기
     */
    fun doOperations(emCommand: Int) = when (emCommand) {
        1 -> { // 전체 사원 조회
            emOperations.viewAllEmployee()
        }
        2 -> { // 부서 별 사원 조회
            val employeeDepartment : Department
            loop@ do {
                println("조회를 원하는 부서를 입력하세요")
                println("[1000] 개발 팀 \n[1001] 영업 팀\n[1002] 고객 대응 팀\n[1003] 사무직")
                val emDepartment : Int = try {
                    emScanner.nextLine().toInt()
                } catch(e:Exception) { -1 }
                when(emDepartment) {
                    1000 -> {
                        employeeDepartment = RND_LAB
                        break@loop
                    }
                    1001 -> {
                        employeeDepartment = MARKETING_TEAM
                        break@loop
                    }
                    1002 -> {
                        employeeDepartment = CS_TEAM
                        break@loop
                    }
                    1003 -> {
                        employeeDepartment = GENERAL_AFFAIRS
                        break@loop
                    }
                    else -> println("존재하지 않는 부서입니다\n해당되는 부서 번호를 다시 입력해주세요")
                }
            } while (true)
            emOperations.viewDeptEmployee(employeeDepartment)
        }
        3 -> { // 사원 번호로 조회
            loop@do {
                println("조회를 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                when (val employeeId = emScanner.nextLine()) {
                    "취소" -> break@loop
                    else -> {
                        when ( try { emOperations.isValidEmployee(UUID.fromString(employeeId)) } catch (e: Exception) { -1 } ) {
                            in 0..2 -> {
                                emOperations.viewEmployeeById(UUID.fromString(employeeId))
                                break@loop
                            }
                            else -> {
                                println("사원 번호 $employeeId 조회 실패\n존재하지 않는 사원 번호입니다")
                            }
                        }
                    }
                }
            } while (true)
        }
        4 -> { // 신규 사원 등록
            val employee: Employee = registerEmployee()
            when(emOperations.createEmployee(employee)) {
                null -> println("신규 사원 등록 실패\n알 수 없는 오류입니다")
                else -> println("$employee\n신규 사원 등록 성공")
            }
        }
        5 -> { // 기존 사원 정보 변경
            loop@do {
                var modifyFlag: Int
                println("정보 변경을 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                when (val employeeId = emScanner.nextLine()) {
                    "취소" -> break@loop
                    else -> {
                        modifyFlag = try {
                            emOperations.isValidEmployee(UUID.fromString(employeeId))
                        } catch (e: Exception) {
                            -1
                        }
                        when (modifyFlag) {
                            0 -> {
                                val employee: PermanentEmployee = modifyEmployee(employeeId, modifyFlag) as PermanentEmployee
                                employee.let { emOperations.updateEmployee(it) }
                                break@loop
                            }
                            1 -> {
                                val employee: SalesEmployee = modifyEmployee(employeeId, modifyFlag) as SalesEmployee
                                employee.let { emOperations.updateEmployee(it) }
                                break@loop
                            }
                            2 -> {
                                val employee: PartTimeEmployee = modifyEmployee(employeeId, modifyFlag) as PartTimeEmployee
                                employee.let { emOperations.updateEmployee(it) }
                                break@loop
                            }
                            else -> println("$modifyFlag 사원 번호 $employeeId 정보 변경 실패\n존재하지 않는 사원 번호입니다")
                        }
                    }
                }
            } while (true)
        }
        6 -> { // 기존 사원 삭제
            loop@do {
                println("삭제를 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                when (val employeeId = emScanner.nextLine()) {
                    "취소" -> break
                    else -> {
                        when (try {
                            emOperations.isValidEmployee(UUID.fromString(employeeId))
                        } catch (e: Exception) {
                            -1
                        }) {
                            in 0..2 -> {
                                when(emOperations.deleteEmployee(UUID.fromString(employeeId))) {
                                    true -> println("사원 번호 $employeeId 삭제 완료")
                                    false -> println("사원 번호 $employeeId 삭제 실패\n알 수 없는 오류입니다")
                                }
                                break@loop
                            }
                            -1 -> println("사원 번호 $employeeId 삭제 실패\n존재하지 않는 사원 번호입니다")
                        }
                    }
                }
            } while (true)
        }
        7 -> exitProcess(0) // 종료
        else -> println("잘못된 입력입니다") // 예외처리
    }

    /**
     * 사원 정보 변경을 선택 시, 사용자에게 변경 정보를 입력받고 MutableMap 컬렉션으로 임시 저장
     * 기본 정보(이름, 나이, 주소, 부서)는 Permanent, Sales, PartTime 모든 사원이 공통적으로 가짐
     * 따라서 이 정보는 Employee 클래스에 있는 modifyEmployeeInformation 메서드를 통해 처리
     * 급여 정보(월급, 영업실적, 일일 근무시간)는 각 클래스가 모두 다른 변수를 사용하므로 메서드도 각각 다르게 처리
     */
    private fun modifyEmployee(employeeId:String, employeePart:Int): Employee? {
        val mapModifyInfo : MutableMap<String,String> = mutableMapOf()
            loop@ do {
                println("변경을 원하는 정보를 입력하세요")
                println("\n[1] 이름\n[2] 나이\n[3] 주소\n[4] 부서\n[5] 완료")
                when (try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                    1 -> { // ID로 조회된 사원의 변경할 이름을 입력 받고 MutableMap 컬렉션에 추가
                        println("이름을 입력하세요")
                        mapModifyInfo.put("name",emScanner.nextLine())
                    }
                    2 -> { // ID로 조회된 사원의 변경할 나이를 입력 받고 MutableMap 컬렉션에 추가
                        loop@ do {
                            println("나이를 입력하세요")
                            val emAge : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
                            when {
                                emAge < 0 -> println("잘못된 입력입니다")
                                else -> {
                                    mapModifyInfo.put("age",emAge.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                    }
                    3 -> { // ID로 조회된 사원의 변경할 주소를 입력 받고 MutableMap 컬렉션에 추가
                        println("주소를 입력하세요")
                        mapModifyInfo.put("address",emScanner.nextLine())
                    }
                    4 -> { // ID로 조회된 사원의 변경할 부서를 입력 받고 MutableMap 컬렉션에 추가
                        loop@ do {
                            println("부서를 입력하세요")
                            println("[1000] 개발 팀 \n[1001] 영업 팀\n[1002] 고객 대응 팀\n[1003] 사무직")
                            val emDepartment = try {
                                emScanner.nextLine().toInt()
                            } catch(e:Exception) { -1 }
                            when(emDepartment) {
                                1000 -> {
                                    mapModifyInfo.put("department","RND_LAB")
                                    break@loop
                                }
                                1001 -> {
                                    mapModifyInfo.put("department","MARKETING_TEAM")
                                    break@loop
                                }
                                1002 -> {
                                    mapModifyInfo.put("department","CS_TEAM")
                                    break@loop
                                }
                                1003 -> {
                                    mapModifyInfo.put("department","GENERAL_AFFAIRS")
                                    break@loop
                                }
                                else -> println("존재하지 않는 부서입니다\n해당되는 부서 번호를 다시 입력해주세요")
                            }
                        } while (true)
                    }
                    5 -> { // 변경할 정보 입력 완료
                           // ID로 조회된 사원의 정규 사원, 영업 사원, 파트 타임 여부를 확인하여 해당되는 월급 변수를 입력 받고 MutableMap 컬렉션에 추가
                        when(emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart).getEmployeePart()) {
                            2 -> { // ID로 조회된 사원이 파트 타임 사원일 때
                                   // 일일 근무 시간을 입력 받고 MutableMap 컬렉션에 추가
                                val employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as PartTimeEmployee
                                loop@ do {
                                    println("일간 근무 시간을 시간 단위로 입력하세요")
                                    val emWorkingHour : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
                                    when {
                                        emWorkingHour < 0 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("workinghour", emWorkingHour.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                                employee.modifyEmployeeInformation( try { mapModifyInfo.get("name").toString() } catch(e:Exception) { null },
                                    try { valueOf(mapModifyInfo.get("department").toString()) } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("age")?.toInt() } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("address").toString() } catch(e:Exception) { null } )
                                employee.modifyPTEmployeeSalary( try { mapModifyInfo.get("workinghour")?.toLong() } catch(e:Exception) { null } )
                                return employee
                            }
                            1 -> { // ID로 조회된 사원이 영업 사원일 때
                                   // 연간 급여 총액, 연간 영업 실적 총액을 입력 받고 MutableMap 컬렉션에 추가
                                val employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as SalesEmployee
                                loop@ do {
                                    println("연간 급여 총액을 원 단위로 입력하세요")
                                    val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
                                    when {
                                        emSalary < 0 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salary", emSalary.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                                loop@ do {
                                    println("연간 영업 실적 총액을 원 단위로 입력하세요")
                                    val emSalesPerformance : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
                                    when {
                                        emSalesPerformance < 0 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salesperformance", emSalesPerformance.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                                employee.modifyEmployeeInformation( try { mapModifyInfo.get("name").toString() } catch(e:Exception) { null },
                                    try { valueOf(mapModifyInfo.get("department").toString()) } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("age")?.toInt() } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("address").toString() } catch(e:Exception) { null } )
                                employee.modifySEmployeeSalary( try { mapModifyInfo.get("salary")?.toLong() } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("salesperformance")?.toLong() } catch(e:Exception) { null } )
                                return employee
                            }
                            0 -> { // ID로 조회된 사원이 정규 사원일 때
                                   // 연간 급여 총액을 입력 받고 MutableMap 컬렉션에 추가
                                val employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as PermanentEmployee
                                loop@ do {
                                    println("연간 급여 총액을 원 단위로 입력하세요")
                                    val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
                                    when {
                                        emSalary < 0 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salary", emSalary.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                                employee.modifyEmployeeInformation( try { mapModifyInfo.get("name").toString() } catch(e:Exception) { null },
                                    try { valueOf(mapModifyInfo.get("department").toString()) } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("age")?.toInt() } catch(e:Exception) { null },
                                    try { mapModifyInfo.get("address") } catch(e:Exception) { null } )
                                employee.modifyPEmployeeSalary( try { mapModifyInfo.get("salary")?.toLong() } catch(e:Exception) { null } )
                                return employee
                            }
                            else -> { return null }
                        }
                        @Suppress("UNREACHABLE_CODE") // 로직 상 모든 경우에 return 진행되어 닿지는 않으나, 명시는 필요해보여 작성하였음
                        break@loop
                    }
                    else -> println("잘못된 입력입니다")
                }
            } while (true)
    }
    /**
     * 신규 사원 등록을 선택 시, 사용자에게 등록 정보를 입력받고 ArrayList 컬렉션으로 임시 저장
     * 기본 정보(부서, 이름, 나이, 주소)와 사원 구분 정보(정규, 영업, 파트타임)는 Permanent, Sales, PartTime 모든 사원이 공통적으로 가짐
     * 따라서 이 정보는 각 사원 클래스로 상속된 Employee 클래스의 생성자에 파라미터로 전달되어 사원 구분 정보에 해당하는 클래스로 사원 객체를 생성
     * 급여 정보(월급, 영업실적, 일일 근무시간)는 각 클래스가 모두 다른 변수를 사용하므로 사원 구분 정보에 따라 분기하여 입력 받아 처리
     */
    private fun registerEmployee(): Employee {
        val arrayRegisterInfo = ArrayList<String>()
        loop@ do {
            println("등록하실 사원 정보를 입력하세요")
            loop@ do {
                println("부서")
                println("[1000] 개발 팀 \n[1001] 영업 팀\n[1002] 고객 대응 팀\n[1003] 사무직")
                when ( try {
                    emScanner.nextLine().toInt().also { it }
                } catch (e:Exception) { null } ) {
                    1000 -> {
                        arrayRegisterInfo.add("RND_LAB")
                        break
                    }
                    1001 -> {
                        arrayRegisterInfo.add("MARKETING_TEAM")
                        break
                    }
                    1002 -> {
                        arrayRegisterInfo.add("CS_TEAM")
                        break
                    }
                    1003 -> {
                        arrayRegisterInfo.add("GENERAL_AFFAIRS")
                        break@loop
                    }
                    else -> println("존재하지 않는 부서입니다\n해당되는 부서 번호를 다시 입력해주세요")
                }
            } while (true)
            println("이름")
            arrayRegisterInfo.add(emScanner.nextLine())
            loop@ do {
                println("나이")
                when(val emAge : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                    -1 -> println("잘못된 입력입니다")
                    else -> {
                        arrayRegisterInfo.add(emAge.toString())
                        break@loop
                    }
                }
            } while (true)
            println("주소")
            arrayRegisterInfo.add(emScanner.nextLine())
            loop@ do {
                println("구분")
                println("[1] 정규 사원\n[2] 영업 사원\n[3] 파트 타임")
                when (try {
                    emScanner.nextLine().toInt().also { arrayRegisterInfo.add(it.toString()) }
                } catch (e: Exception) {
                    null
                }) {
                    in 1..3 -> {  }
                    else -> {
                        println("잘못된 입력입니다\n1 ~ 3 숫자 중 해당되는 사원 구분을 다시 입력해주세요")
                        break@loop
                    }
                }
                when(arrayRegisterInfo[4].toInt()) {
                    1 -> {
                        loop@ do {
                            println("연간 급여 총액을 원 단위로 입력하세요")
                            when(val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    arrayRegisterInfo.add(emSalary.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                        when(val employee = PermanentEmployee(makeUuid(), arrayRegisterInfo[4].toInt(), arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[5].toLong())) {
                            else -> return employee
                        }
                        @Suppress("UNREACHABLE_CODE") // 로직 상 모든 경우에 return 진행되어 닿지는 않으나, 명시는 필요해보여 작성하였음
                        break@loop
                    }
                    2 -> {
                        loop@ do {
                            println("연간 급여 총액을 원 단위로 입력하세요")
                            when(val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    arrayRegisterInfo.add(emSalary.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                        loop@ do {
                            println("연간 영업 실적 총액을 원 단위로 입력하세요")
                            when(val emSalesPerformance : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    arrayRegisterInfo.add(emSalesPerformance.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                        when(val employee = SalesEmployee(makeUuid(), arrayRegisterInfo[4].toInt(), arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[5].toLong(),arrayRegisterInfo[6].toLong())) {
                            else -> return employee
                        }
                        @Suppress("UNREACHABLE_CODE") // 로직 상 모든 경우에 return 진행되어 닿지는 않으나, 명시는 필요해보여 작성하였음
                        break@loop
                    }
                    3 -> {
                        loop@ do {
                            println("일간 근무 시간을 시간 단위로 입력하세요")
                            when(val emWorkingHour : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    arrayRegisterInfo.add(emWorkingHour.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                        when(val employee = PartTimeEmployee(makeUuid(), arrayRegisterInfo[4].toInt(), arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[5].toLong())) {
                            else -> return employee
                        }
                        @Suppress("UNREACHABLE_CODE") // 로직 상 모든 경우에 return 진행되어 닿지는 않으나, 명시는 필요해보여 작성하였음
                        break@loop
                    }
                    else -> { }
                }
            } while (true)
        } while (true)
    }
}
