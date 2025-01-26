package com.aivle.project.dto;

import com.aivle.project.enums.Dept;
import lombok.*;

import java.time.LocalDate;

public class EmployeeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Post{
        private String employeeId;
        private String employeeName;
        private String password;
        private String position;
        private String dept;
        private String team;
        private String hireDate;
        private Float baseSalary;
        private String accessPermission;
        private String passwordAnswer;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Get{
        private String employeeId;
        private String employeeName;
        private LocalDate hireDate;
        private LocalDate terminationDate;
        private float baseSalary;
        private String position;
        private String accessPermission;
        private String dept;
        private String team;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Patch{
        private String employeeId;
        private String existPassword;
        private String newPassword;
        private String passwordAnswer;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class GetId{
        private String employeeId;
        private String employeeName; // detail페이지 로딩 속도를 높이기 위해 전체 데이터 조회에서 id와 name만 가져오게 변경
        private Dept departmentId; // 부서 정보 추가
    }

}
