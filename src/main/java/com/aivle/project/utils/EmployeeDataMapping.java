package com.aivle.project.utils;

import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Team;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EmployeeDataMapping {

    public static final Map<Position, Float> POSITION_BASE_SALARY_MAP = new HashMap<>();
    public static final Map<Dept, String> DEPT_MAP = new HashMap<>();
    public static final Map<Position, String> POSITION_MAP = new HashMap<>();
    public static final Map<Team, String> TEAM_MAP = new HashMap<>();

    static {
        // 직급별 기본 연봉 매핑
        POSITION_BASE_SALARY_MAP.put(Position.STAFF, 50000000F);
        POSITION_BASE_SALARY_MAP.put(Position.JUNIOR, 60000000F);
        POSITION_BASE_SALARY_MAP.put(Position.ASSOCIATE, 75000000F);
        POSITION_BASE_SALARY_MAP.put(Position.MANAGER, 100000000F);
        POSITION_BASE_SALARY_MAP.put(Position.ASSISTANT_MANAGER, 120000000F);
        POSITION_BASE_SALARY_MAP.put(Position.TEAM_LEADER, 140000000F);
        POSITION_BASE_SALARY_MAP.put(Position.DEPARTMENT_HEAD, 160000000F);
        POSITION_BASE_SALARY_MAP.put(Position.GENERAL_MANAGER, 180000000F);
    }

    static {
        // 부서 매핑
        DEPT_MAP.put(Dept.STRATEGY_DEPT, "전략고객본부");
        DEPT_MAP.put(Dept.PUBLIC_DEPT, "공공고객본부");
        DEPT_MAP.put(Dept.FINANCE_DEPT, "금융고객본부");
        DEPT_MAP.put(Dept.CORPORATE_DEPT, "법인영업본부");
    }

    static {
        // 직급명 매핑
        POSITION_MAP.put(Position.GENERAL_MANAGER, "본부장");
        POSITION_MAP.put(Position.DEPARTMENT_HEAD, "부장");
        POSITION_MAP.put(Position.TEAM_LEADER, "팀장");
        POSITION_MAP.put(Position.ASSISTANT_MANAGER, "차장");
        POSITION_MAP.put(Position.MANAGER, "과장");
        POSITION_MAP.put(Position.ASSOCIATE, "대리");
        POSITION_MAP.put(Position.JUNIOR, "주임");
        POSITION_MAP.put(Position.STAFF, "사원");
    }

    static {
        // 팀명 매핑
        TEAM_MAP.put(Team.STRATEGY_CUST_SECTOR, "전략고객섹터담당");
        TEAM_MAP.put(Team.STRATEGY_CUST_1, "전략고객1담당");
        TEAM_MAP.put(Team.STRATEGY_CUST_2, "전략고객2담당");
        TEAM_MAP.put(Team.PUBLIC_CUST_SECTOR, "공공고객섹터담당");
        TEAM_MAP.put(Team.PUBLIC_CUST_1, "공공고객1담당");
        TEAM_MAP.put(Team.PUBLIC_CUST_2, "공공고객2담당");
        TEAM_MAP.put(Team.FINANCE_CUST_SECTOR, "금융고객섹터담당");
        TEAM_MAP.put(Team.FINANCE_CUST_1, "금융고객1담당");
        TEAM_MAP.put(Team.FINANCE_CUST_2, "금융고객2담당");
        TEAM_MAP.put(Team.CORPORATE_SALES_PLANNING, "법인영업기획담당");
        TEAM_MAP.put(Team.CORPORATE_CUST, "법인고객담당");
        TEAM_MAP.put(Team.CORPORATE_RETAIL, "법인유통담당");
        TEAM_MAP.put(Team.CORPORATE_SALES_SECTOR, "법인섹터담당");
    }
}