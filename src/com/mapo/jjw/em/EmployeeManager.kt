package com.mapo.jjw.em

import com.mapo.jjw.em.model.*
import com.mapo.jjw.em.model.Department.*
import com.mapo.jjw.em.operations.EMOperations
import com.mapo.jjw.em.operations.EMOperationsImpl
import java.lang.Exception
import java.util.*
import kotlin.system.exitProcess
import java.util.UUID
import kotlin.collections.ArrayList

const val TAX_RATE = 0.08F
const val HOURLY_RATE = 25000

class EmployeeManager {
    private val emScanner = Scanner(System.`in`)
    private val emOperations = EMOperationsImpl()
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
    fun doOperations(emCommand: Int) {
        when (emCommand) {
            1 -> {
                emOperations.viewAllEmployee()
            }
            2 -> {
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
            3 -> {
                Loop@do {
                    println("조회를 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                    when (val employeeId = emScanner.nextLine()) {
                        "취소" -> break
                        else -> {
                            when ( try { emOperations.isValidEmployee(UUID.fromString(employeeId)) } catch (e: Exception) { false } ) {
                                true -> {
                                    emOperations.viewEmployeeById(UUID.fromString(employeeId))
                                    break@Loop
                                }
                                false -> println("사원 번호 $employeeId 조회 실패\n존재하지 않는 사원 번호입니다")
                            }
                        }
                    }
                } while (true)
            }
            4 -> {
                val employee: Employee = registerEmployee()
                when(emOperations.createEmployee(employee)) {
                    null -> println("신규 사원 등록 실패\n알 수 없는 오류입니다")
                    else -> println("$employee\n신규 사원 등록 성공")
                }
            }
            5 -> {
                Loop@do {
                    var modifyFlag : Int = 0
                    println("정보 변경을 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                    when (val employeeId = emScanner.nextLine()) {
                        "취소" -> break
                        else -> {
                            when ( try { modifyFlag = emOperations.isValidEmployee(UUID.fromString(employeeId)) } catch (e: Exception) { false } ) {
                                true -> {
                                    val parentEmployee: Employee = emOperations.getEmployeeById(UUID.fromString(employeeId), modifyFlag)
                                    when(parentEmployee.getEmployeePart()) {
                                        in 0..2 -> {
                                            val employee: Employee = modifyEmployee(employeeId, parentEmployee.getEmployeePart())
                                            emOperations.updateEmployee(employee)
                                            break@Loop
                                        }
                                        else -> println("사원 번호 $employeeId 정보 변경 실패\n존재하지 않는 사원 번호입니다")
                                    }
                                }
                                false -> println("사원 번호 $employeeId 정보 변경 실패\n존재하지 않는 사원 번호입니다")
                            }
                        }
                    }
                } while (true)
            }
            6 -> {
                Loop@do {
                    println("삭제를 원하는 사원 번호를 입력하세요\n[취소] 이전 단계로 이동")
                    when (val employeeId = emScanner.nextLine()) {
                        "취소" -> break
                        else -> {
                            when (try {
                                emOperations.isValidEmployee(UUID.fromString(employeeId))
                            } catch (e: Exception) {
                                false
                            }) {
                                true -> {
                                    when(emOperations.deleteEmployee(UUID.fromString(employeeId))) {
                                        true -> println("사원 번호 $employeeId 삭제 완료")
                                        false -> println("사원 번호 $employeeId 삭제 실패\n알 수 없는 오류입니다")
                                    }
                                    break@Loop
                                }
                                false -> println("사원 번호 $employeeId 삭제 실패\n존재하지 않는 사원 번호입니다")
                            }
                        }
                    }
                } while (true)
            }
            7 -> exitProcess(0)
            else -> println("잘못된 입력입니다")
        }
    }
    private fun modifyEmployee(employeeId:String, employeePart:Int): Employee {
        var employee : Any = 0
            when(employeePart) {
                0 -> {
                    employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as PermanentEmployee
                }
                1 -> {
                    employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as SalesEmployee
                }
                2 -> {
                    employee = emOperations.getEmployeeById(UUID.fromString(employeeId), employeePart) as PartTimeEmployee
                }
                else -> { }
            }
            loop@ do {
                val mapModifyInfo : MutableMap<String,String> = mutableMapOf()
                var emDepartment: Int
                employee as Employee
                println("변경을 원하는 정보를 입력하세요")
                println("\n[1] 이름\n[2] 나이\n[3] 주소\n[4] 부서\n[5] 완료")
                when (try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                    1 -> {
                        println("이름을 입력하세요")
                        mapModifyInfo.put("name",emScanner.nextLine())
                    }
                    2 -> {
                        loop@ do {
                            println("나이를 입력하세요")
                            when(val emAge : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    mapModifyInfo.put("age",emAge.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                    }
                    3 -> {
                        println("주소를 입력하세요")
                        mapModifyInfo.put("address",emScanner.nextLine())
                    }
                    4 -> {
                        loop@ do {
                            println("부서를 입력하세요")
                            println("[1000] 개발 팀 \n[1001] 영업 팀\n[1002] 고객 대응 팀\n[1003] 사무직")
                            emDepartment = try {
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
                    5 -> {
                        when(employee) {
                            is PartTimeEmployee -> {
                                loop@ do {
                                    println("일간 근무 시간을 시간 단위로 입력하세요")
                                    when(val emWorkingHour : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                        -1 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("workingHour", emWorkingHour.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                            }
                            is SalesEmployee -> {
                                loop@ do {
                                    println("연간 급여 총액을 원 단위로 입력하세요")
                                    when(val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                        -1 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salary", emSalary.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                                loop@ do {
                                    println("연간 영업 인센티브 총액을 원 단위로 입력하세요")
                                    when(val emSalesPerformance : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                        -1 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salesperformance", emSalesPerformance.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                            }
                            is PermanentEmployee, !is SalesEmployee, !is PartTimeEmployee -> {
                                loop@ do {
                                    println("연간 급여 총액을 원 단위로 입력하세요")
                                    when(val emSalary : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                        -1 -> println("잘못된 입력입니다")
                                        else -> {
                                            mapModifyInfo.put("salary", emSalary.toString())
                                            break@loop
                                        }
                                    }
                                } while (true)
                            }
                        }
                        println(when(employee) {
                            is PermanentEmployee -> { "정규 사원 객체" }
                            is PartTimeEmployee -> { "파트 타임 객체" }
                            is SalesEmployee -> { "영업 사원 객체" }
                            else -> { "null" }
                        })
                        println(mapModifyInfo.get("name").toString())
                        println(mapModifyInfo.get("department").toString())
                        println(mapModifyInfo.get("age").toString())
                        println(mapModifyInfo.get("address").toString())
                        println(mapModifyInfo.get("salary").toString())

                        try { mapModifyInfo.get("name").toString() } catch(e:Exception) { null }?.let {
                            try { valueOf(mapModifyInfo.get("department").toString()) } catch(e:Exception) { null }?.let { it1 ->
                                try {
                                    mapModifyInfo.get("age")?.toInt()
                                } catch(e:Exception) { null }?.let { it2 ->
                                    try { mapModifyInfo.get("address").toString() } catch(e:Exception) { null }?.let { it3 ->
                                        employee.modifyEmployeeInformation(it, it1, it2, it3)
                                    }
                                }
                            }
                        }

                        /*
                        when(mapModifyInfo.get("salary") && mapModifyInfo.get("salesperformance")) {

                        }
                         */
                        return employee
                        break@loop
                    }
                    else -> println("잘못된 입력입니다")
                }
            } while (true)
    }
    private fun registerEmployee(): Employee {
        loop@ do {
            val arrayRegisterInfo = ArrayList<String>()
            var emDepartment = 0
            var emType = 0
            println("등록하실 사원 정보를 입력하세요")
            loop@ do {
                println("부서")
                println("[1000] 개발 팀 \n[1001] 영업 팀\n[1002] 고객 대응 팀\n[1003] 사무직")
                when ( try {
                    emScanner.nextLine().toInt().also { emDepartment = it }
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
                    emScanner.nextLine().toInt().also { emType = it }
                } catch (e: Exception) {
                    null
                }) {
                    in 1..3 -> { }
                    else -> {
                        println("잘못된 입력입니다\n1 ~ 3 숫자 중 해당되는 사원 구분을 다시 입력해주세요")
                        break@loop
                    }
                }
                when(emType) {
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
                        when(val employee = PermanentEmployee(makeUuid(), emType, arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[4].toLong())) {
                            else -> return employee
                        }
                        break
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
                            println("연간 영업 인센티브 총액을 원 단위로 입력하세요")
                            when(val emSalesPerformance : Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 } ) {
                                -1 -> println("잘못된 입력입니다")
                                else -> {
                                    arrayRegisterInfo.add(emSalesPerformance.toString())
                                    break@loop
                                }
                            }
                        } while (true)
                        when(val employee = SalesEmployee(makeUuid(), emType, arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[4].toLong(),arrayRegisterInfo[5].toLong())) {
                            else -> return employee
                        }
                        break
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
                        when(val employee = PartTimeEmployee(makeUuid(), emType, arrayRegisterInfo[1], valueOf(arrayRegisterInfo[0]),arrayRegisterInfo[2].toInt(),arrayRegisterInfo[3],arrayRegisterInfo[4].toLong())) {
                            else -> return employee
                        }
                        break
                    }
                    else -> null
                }
            } while (true)
        } while (true)
    }
}