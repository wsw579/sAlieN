package com.aivle.project.dto;

import com.aivle.project.Validator.EnumValidator;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class EmployeeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Post {
        @NotBlank(message = "직원 ID는 필수입니다.")
        private String employeeId;

        @NotBlank(message = "직원 이름은 필수입니다.")
        @Size(max = 50, message = "직원 이름은 최대 50자까지 입력 가능합니다.")
        private String employeeName;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자 이내여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$",
                message = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다."
        )
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String confirmPassword;

        @NotBlank(message = "직급을 입력해야 합니다.")
        @EnumValidator(enumClass = Position.class, message = "올바른 직급(Position)을 입력하세요.")
        private String position;

        @NotBlank(message = "부서를 입력해야 합니다.")
        @EnumValidator(enumClass = Dept.class, message = "올바른 부서(Dept)를 입력하세요.")
        private String dept;

        @NotBlank(message = "팀을 입력해야 합니다.")
        @EnumValidator(enumClass = Team.class, message = "올바른 팀(Team)을 입력하세요.")
        private String team;

        @NotNull(message = "입사일을 입력해야 합니다.")
        private String hireDate;

        @PositiveOrZero(message = "기본 급여는 0 이상이어야 합니다.")
        private Float baseSalary;

        @NotBlank(message = "접근 권한을 입력해야 합니다.")
        @EnumValidator(enumClass = Role.class, message = "올바른 접근 권한(Role)을 입력하세요.")
        private String accessPermission;

        @NotBlank(message = "비밀번호 답변을 입력해야 합니다.")
        @Size(max = 30, message = "비밀번호 답변은 최대 30자까지 가능합니다.")
        private String passwordAnswer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Get{
        @NotBlank(message = "직원 ID는 필수입니다.")
        private String employeeId;

        @NotBlank(message = "직원 이름은 필수입니다.")
        private String employeeName;

        @NotNull(message = "입사일은 필수입니다.")
        private LocalDate hireDate;

        private LocalDate terminationDate;

        @PositiveOrZero(message = "기본 급여는 0 이상이어야 합니다.")
        private float baseSalary;

        @NotBlank(message = "직급을 입력해야 합니다.")
        @EnumValidator(enumClass = Position.class, message = "올바른 직급(Position)을 입력하세요.")
        private String position;

        @NotBlank(message = "접근 권한을 입력해야 합니다.")
        @EnumValidator(enumClass = Role.class, message = "올바른 접근 권한(Role)을 입력하세요.")
        private String accessPermission;

        @NotBlank(message = "부서를 입력해야 합니다.")
        @EnumValidator(enumClass = Dept.class, message = "올바른 부서(Dept)를 입력하세요.")
        private String dept;

        @NotBlank(message = "팀을 입력해야 합니다.")
        @EnumValidator(enumClass = Team.class, message = "올바른 팀(Team)을 입력하세요.")
        private String team;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Patch{
        @NotBlank(message = "직원 ID는 필수입니다.")
        private String employeeId;

        @NotBlank(message = "현재 비밀번호를 입력해야 합니다.")
        private String existPassword;

        @NotBlank(message = "새 비밀번호를 입력해야 합니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자 이내여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$",
                message = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다."
        )
        private String newPassword;

        @NotBlank(message = "비밀번호 답변을 입력해야 합니다.")
        @Size(max = 30, message = "비밀번호 답변은 최대 30자까지 가능합니다.")
        private String passwordAnswer;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class GetId{
        @NotBlank(message = "직원 ID는 필수입니다.")
        private String employeeId;

        @NotBlank(message = "직원 이름은 필수입니다.")
        private String employeeName;

        @NotNull(message = "부서 정보는 필수입니다.")
        @EnumValidator(enumClass = Dept.class, message = "올바른 부서(Dept)를 입력하세요.")
        private Dept departmentId;
    }

}
