package com.aivle.project.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "leads")
public class LeadsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leadId;

    @Column(nullable = false, length = 50)
    private String leadStatus;

    @Column(nullable = false, length = 255)
    private String leadSource;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetCloseDate;

    @Column(nullable = false)
    private String customerRequirements;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String customerRepresentitive;

    @Column(nullable = false)
    private String c_tel;

    // Employee 외래키 설정
    @ManyToOne  // Many Leads to One Employee
    @JoinColumn(name = "employee_id",  nullable = true)
    private EmployeeEntity employee;

    // Account 외래키 설정
    @ManyToOne// Many Leads to One Account
    @JoinColumn(name = "account_id",  nullable = true)
    private AccountEntity account;
}
