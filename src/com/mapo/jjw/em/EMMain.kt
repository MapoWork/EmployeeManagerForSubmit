package com.mapo.jjw.em

import java.lang.Exception
import java.util.*

fun main() {
    val emScanner = Scanner(System.`in`)
    val emTaskRunner = EmployeeManager()
    do {
        println("\n사원 관리 프로그램\n")
        println("[1] 전체 사원 조회\n[2] 부서별 사원 조회\n[3] 사원 번호 조회\n" +
                "[4] 신규 사원 등록\n[5] 사원 정보 변경\n[6] 사원 정보 삭제\n[7] 종료\n" +
                "원하는 메뉴에 해당하는 숫자를 입력하신 후 엔터키를 누르세요")
        val emCommand: Int = try { emScanner.nextLine().toInt() } catch (e:Exception) { -1 }
        emTaskRunner.doOperations(emCommand)
    } while (true)
}