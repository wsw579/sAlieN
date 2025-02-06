package com.aivle.project.dto;


import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.ProductsEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContractsDto {

    @NotNull(message = "Contract ID는 필수입니다.")
    private Long contractId;

    @NotBlank(message = "계약 상태는 필수입니다.")
    @Pattern(regexp = "^(Draft|In Approval Process|Activated)$", message = "계약 상태는 'Draft', 'In Approval Process', 'Activated' 중 하나여야 합니다.")
    private String contractStatus;

    @NotNull(message = "계약 시작일은 필수입니다.")
    @PastOrPresent(message = "계약 시작일은 과거 또는 현재여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "계약 종료일은 필수입니다.")
    @FutureOrPresent(message = "계약 종료일은 미래 또는 현재여야 합니다.")
    private LocalDate terminationDate;

    @Size(max = 500, message = "계약 상세 내용은 500자 이하로 입력해야 합니다.")
    private String contractDetail;

    @PositiveOrZero(message = "계약 매출은 0 이상이어야 합니다.")
    private float contractSales;

    @PositiveOrZero(message = "계약 금액은 0 이상이어야 합니다.")
    private float contractAmount;

    @NotBlank(message = "계약 분류는 필수입니다.")
    @Pattern(regexp = "^(None|완판|임대|할부|재계약|이관)$", message = "계약 분류는 '선택없음', '완판', '임대', '할부', '재계약', '이관' 중 하나여야 합니다.")
    private String contractClassification;
//    private String uploadedFilePath;

    // 파일 관련 필드
    private byte[] fileData;
    private String fileName;
    private String mimeType;

    // 외래키 부분

    private AccountEntity accountId;
    private EmployeeEntity employeeId;
    private ProductsEntity productId;
    private OpportunitiesEntity opportunityId;





}
