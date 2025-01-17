package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.entity.OpportunitiesEntity;

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


public class AdditionalOpportunitiesDto {
    private Long additionalopportunityId;
    private Long additionalopportunityQuantity;
    private Long additionalopportunitySales;
    private Long additionalopportunityStatus;
    private LocalDate createdDate;
    private LocalDate targetCloseDate;

    //외래키
    private AccountEntity accountId;
    private ProductsEntity productId;
    private OpportunitiesEntity opportunityId;
}
