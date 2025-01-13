package com.aivle.project.dto;


import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.ProductsEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContractsDto {

    private Long contractId;
    private String contractStatus;
    private LocalDate startDate;
    private LocalDate terminationDate;
    private String contractDetail;
    private float contractSales;
    private float contractAmount;
    private String contractClassification;
    private boolean contractSelected;
    private boolean contractDeleted = false;

    // 외래키 부분

    private OpportunitiesEntity opportunityId;

//    private AccountsEntity accountId;
//    private EmployeeEntity employee;
//    private ProductsEntity productId;





}
