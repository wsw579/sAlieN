package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LeadsDto {
    private Long leadId;

    @NotBlank(message = "리드 상태는 필수입니다.")
    @Pattern(regexp = "^(Proposal|Under Review|Accepted|Rejected|Pending)$", message = "리드 상태는 'Proposal', 'Under Review', 'Accepted', 'Rejected', 'Pending' 중 하나여야 합니다.")
    private String leadStatus;

    @NotBlank(message = "리드 소스는 필수입니다.")
    @Size(max = 255, message = "리드 소스는 255자 이내로 입력해야 합니다.")
    private String leadSource;

    @NotNull(message = "리드 시작일은 필수입니다.")
    @PastOrPresent(message = "리드 시작일은 과거 또는 현재여야 합니다.")
    private LocalDate createdDate;

    @NotNull(message = "리드 종료일은 필수입니다.")
    @FutureOrPresent(message = "리드 종료일은 미래 또는 현재여야 합니다.")
    private LocalDate targetCloseDate;

    @NotBlank(message = "고객 문의사항는 필수입니다.")
    @Size(max = 1000, message = "고객 문의사항은 1,000자 이내로 입력해야 합니다.")
    private String customerRequirements;

    @NotNull(message = "회사명은 필수입니다.")
    private String companyName;

    @NotNull(message = "직원 입력은 필수입니다.")
    private String customerRepresentitive;

    @NotBlank(message = "직원 번호 입력은 필수입니다.")
    @Pattern(regexp = "^(\\+\\d{1,3}-)?(01[016789]-\\d{3,4}-\\d{4}|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호를 입력하세요. (예: +82-10-1234-5678, 010-1234-5678 또는 02-123-4567)")
    private String c_tel;

    //외래키
    private AccountEntity accountId;
    private EmployeeEntity employeeId;
}
