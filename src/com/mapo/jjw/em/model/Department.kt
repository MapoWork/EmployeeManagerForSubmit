package com.mapo.jjw.em.model

enum class Department(var deptCode:Int, var deptName: String) {
    RND_LAB(1000, "개발 팀"),
    MARKETING_TEAM(1001, "영업 팀"),
    CS_TEAM(1002, "고객 대응 팀"),
    GENERAL_AFFAIRS(1003, "사무직")
}