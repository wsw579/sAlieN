package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    private String accountName;

    @NotBlank(message = "계정 유형 입력은  필수입니다.")
    private String accountType;

    @NotBlank(message = "웹사이트 입력은 필수입니다.")
    @URL(message = "올바른 웹사이트 URL을 입력해주세요.")
    private String website;

    @NotBlank(message = "대표전화 입력은 필수입니다.")
    @Pattern(regexp = "^(\\+\\d{1,3}-)?(01[016789]-\\d{3,4}-\\d{4}|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호를 입력하세요. (예: +82-10-1234-5678, 010-1234-5678 또는 02-123-4567)")
    private String contact;

    @NotBlank(message = "사업 유형 입력은 필수입니다.")
    private String businessType;

    @NotBlank(message = "직원 입력은 필수입니다.")
    private String accountManager;

    @NotBlank(message = "설명란 입력은 필수입니다.")
    private String accountDetail;

    @NotBlank(message = "배송지 입력은 필수입니다.")
    private String address;

    @NotBlank(message = "직원 번호 입력은 필수입니다.")
    @Pattern(regexp = "^(\\+\\d{1,3}-)?(01[016789]-\\d{3,4}-\\d{4}|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호를 입력하세요. (예: +82-10-1234-5678, 010-1234-5678 또는 02-123-4567)")
    private String accountManagerContact;

    @NotBlank(message = "계정 상태 입력은 필수입니다.")
    private String accountStatus;

    private LocalDate accountCreatedDate;

    // Self-join
    private AccountEntity parentAccountId; // 상위 계정 이름
    //private AccountDto parentAccountDto; // 새로운 상위 계정 생성을 위한 정보

    // 필요한 경우 하위 계정을 포함
    // private List<AccountDto> childAccounts;

    // 외래키 부분
    private EmployeeEntity employeeId;

}
