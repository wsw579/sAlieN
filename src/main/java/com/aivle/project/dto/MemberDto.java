package com.aivle.project.dto;

import lombok.*;

public class MemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Post{
        private String userId;
        private String name;
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
        private String id;
        private String name;
        private String phone;
        private String email;
    }
}
