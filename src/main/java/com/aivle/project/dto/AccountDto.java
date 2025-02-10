package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long accountId;

    @NotBlank(message = "계정 이름 입력은 필수입니다.")
    @Size(max = 50, message = "계정 이름은 50자 이하로 입력해야 합니다.")
    private String accountName;

    @NotBlank(message = "계정 유형 입력은  필수입니다.")
    @Size(max = 50, message = "계정 유형은 50자 이하로 입력해야 합니다.")
    private String accountType;

    @NotBlank(message = "웹사이트 입력은 필수입니다.")
    @URL(message = "올바른 웹사이트 URL을 입력해주세요.")
    @Size(max = 50, message = "웹사이트는 50자 이하로 입력해야 합니다.")
    private String website;

    @NotBlank(message = "대표전화 입력은 필수입니다.")
    @Pattern(regexp = "^(\\+\\d{1,3}-)?(01[016789]-\\d{3,4}-\\d{4}|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호를 입력하세요. (예: +82-10-1234-5678, 010-1234-5678 또는 02-123-4567)")
    @Size(max = 50, message = "대표 전화는 50자 이하로 입력해야 합니다.")
    private String contact;

    @NotBlank(message = "사업 유형 입력은 필수입니다.")
    @Pattern(regexp = "^(제조업|IT서비스|소프트웨어|반도체|전자부품|통신|보안)$", message = "계정 상태는 '제조업', 'IT서비스', '소프트웨어', '반도체', '전자부품', '통신', '보안' 중 하나여야 합니다.")
    private String businessType;

    @NotBlank(message = "직원 입력은 필수입니다.")
    @Size(max = 50, message = "직원 이름은 50자 이하로 입력해야 합니다.")
    private String accountManager;

    @NotBlank(message = "설명란 입력은 필수입니다.")
    @Size(max = 50, message = "계정 설명은 50자 이하로 입력해야 합니다.")
    private String accountDetail;

    @NotBlank(message = "배송지 입력은 필수입니다.")
    @Size(max = 50, message = "배송지는 50자 이하로 입력해야 합니다.")
    private String address;

    @NotBlank(message = "직원 번호 입력은 필수입니다.")
    @Pattern(regexp = "^(\\+\\d{1,3}-)?(01[016789]-\\d{3,4}-\\d{4}|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호를 입력하세요. (예: +82-10-1234-5678, 010-1234-5678 또는 02-123-4567)")
    @Size(max = 50, message = "직원 번호는 50자 이하로 입력해야 합니다.")
    private String accountManagerContact;

    @NotBlank(message = "계정 상태 입력은 필수입니다.")
    @Pattern(regexp = "^(Active|Closed)$", message = "계정 상태는 'Active', 'Closed' 중 하나여야 합니다.")
    private String accountStatus;

    @NotNull(message = "계정 생성일은 필수입니다.")
    @PastOrPresent(message = "계정 생성일은 과거 또는 현재여야 합니다.")
    private LocalDate accountCreatedDate;

    // Self-join
    private AccountEntity parentAccountId; // 상위 계정 이름
    //private AccountDto parentAccountDto; // 새로운 상위 계정 생성을 위한 정보

    // 필요한 경우 하위 계정을 포함
    // private List<AccountDto> childAccounts;

    // 외래키 부분
    private EmployeeEntity employeeId;

}
