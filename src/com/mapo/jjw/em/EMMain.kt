package com.mapo.jjw.em

import java.lang.Exception
import java.util.*

/**
 * 사원 관리 프로그램
 * 메인함수에서 사용자로부터 커맨드를 입력받고 EmployeeManager 객체를 생성하여 파라미터로 넘김
 * 1번부터 7번까지의 메뉴로 프로그램 로직이 반복되며, 7을 누르면 프로그램 종료
 * @author Jungwoo Jo
 * */
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