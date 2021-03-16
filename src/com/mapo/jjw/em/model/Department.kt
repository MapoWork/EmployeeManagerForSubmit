package com.mapo.jjw.em.model

/**
 * Implements an API for Department class
 * Department 클래스는 모든 사원 객체가 가지는 속성인 부서에 대한 형을 제공하는 열거형 클래스
 * @author Jungwoo Jo
 */
enum class Department(var deptCode:Int, var deptName: String) {
    RND_LAB(1000, "개발 팀"),
    MARKETING_TEAM(1001, "영업 팀"),
    CS_TEAM(1002, "고객 대응 팀"),
    GENERAL_AFFAIRS(1003, "사무직")
}