package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.ProductsEntity;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitiesDto {

    @NotNull(message = "Opportunity ID는 필수입니다.")
    private Long opportunityId;

    @NotBlank(message = "기회 명은 필수입니다.")
    private String opportunityName;

    @NotBlank(message = "배송지역은 필수입니다.")
    private String region;

    private int companySize;

    @NotBlank(message = "고객문의사항은 필수입니다.")
    private String opportunityInquiries;

    @NotBlank(message = "고객 담당자는 필수입니다.")
    private String customerEmployee;

    @NotNull(message = "기회 수량은 필수입니다.")
    private float quantity;

    @NotNull(message = "기회 매출은 필수입니다.")
    private float expectedRevenue;

    @NotNull(message = "회사 매출은 필수입니다.")
    private float companyRevenue;

    @NotBlank(message = "기회 세부내용은 필수입니다.")
    private String opportunityNotes;

    @NotNull(message = "생성 일자는 필수입니다.")
    @PastOrPresent(message = "생성 일자는 과거 또는 현재여야 합니다.")
    private LocalDate createdDate;

    @NotNull(message = "마감 기한은 필수입니다.")
    private LocalDate targetCloseDate;

    @NotBlank(message = "기회 상태는 필수입니다.")
    @Pattern(regexp = "^(Qualification|Needs Analysis|Proposal|Negotiation|Closed\\(won\\)|Closed\\(loss\\)|Pending)$",
            message = "기회 상태는 'Qualification', 'Needs Analysis', 'Proposal', 'Negotiation', 'Closed(won)', 'Closed(loss)', 'Pending' 중 하나여야 합니다.")
    private String opportunityStatus;
    private String successRate;


    // 외래키 부분
    private LeadsEntity leadId;
    private AccountEntity accountId;
    private ProductsEntity productId;
    private EmployeeEntity employeeId;
}
