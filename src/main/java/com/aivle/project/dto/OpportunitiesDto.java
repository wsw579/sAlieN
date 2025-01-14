package com.aivle.project.dto;


import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.ProductsEntity;
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
public class OpportunitiesDto {

    private Long opportunityId;
    private String opportunityName;
    private String region;
    private int companySize;
    private String opportunityInquiries;
    private String customerEmployee;
    private float quantity;
    private float expectedRevenue;
    private float companyRevenue;
    private String opportunityNotes;
    private LocalDate createdDate;
    private LocalDate targetCloseDate;
    private String opportunityStatus;
    private String successRate;


    // 외래키 부분
    private LeadsEntity leadId;
    private AccountEntity accountId;
    private ProductsEntity productId;
    private EmployeeEntity employeeId;





}
