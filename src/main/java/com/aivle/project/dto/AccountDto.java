package com.aivle.project.dto;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import lombok.*;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long accountId;
    private String accountName;
    private String accountType;
    private String website;
    private String contact;
    private String businessType;
    private String accountManager;
    private String accountDetail;
    private String address;
    private String accountManagerContact;
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
