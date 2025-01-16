package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LeadsDto {
    private Long leadId;
    private String leadStatus;
    private String leadSource;
    private LocalDate createdDate;
    private LocalDate targetCloseDate;
    private String customerRequirements;
    private String companyName;
    private String customerRepresentitive;
    private String c_tel;

    //외래키
    private AccountEntity accountId;
    private EmployeeEntity employeeId;
}
