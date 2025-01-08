package com.aivle.project.entity;

import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeEntity {
    @Id
    @Column(length = 50, nullable = false)
    private String employeeId;

    @Column(length = 50, nullable = false)
    private String employeeName;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column
    private LocalDate terminationDate;

    @Column
    private float baseSalary;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Role accessPermission;

    @Column(nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String passwordAnswer;
}
