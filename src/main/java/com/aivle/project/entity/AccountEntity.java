package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="accounts")
public class AccountEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id" , nullable = false)
    private Long accountId;


    // 상위계정 셀프조인
    @ManyToOne
    @JoinColumn(name = "parent_account_id",  nullable = true , foreignKey = @ForeignKey(name = "fk_accounts_parent_account_id"))
    private AccountEntity parentAccount;

    // 하위계정 리스트
    @OneToMany(mappedBy = "parentAccount" , cascade = {CascadeType.PERSIST , CascadeType.MERGE, CascadeType.REFRESH , CascadeType.DETACH })
    private List<AccountEntity> childAccounts;


    @Column(name = "account_created_date" )
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountCreatedDate;

    @Column(nullable = false, length = 50)              //  계정명 : 고객사 회사명
    private String accountName;

    @Column(nullable = false, length = 50)
    private String accountType;

    @Column(nullable = false, length = 50)              // 고객사 사이트
    private String website;

    @Column(nullable = false, length = 50)              // 고객사 대표전화
    private String contact;

    @Column(nullable = false, length = 50)
    private String businessType;

    @Column(nullable = false, length = 50)              // 고객사 직원명
    private String accountManager;

    @Column(nullable = false, length = 50)              // 고객사 설명
    private String accountDetail;

    @Column(nullable = false, length = 50)             // 고객사 주소
    private String address;

    @Column(nullable = false, length = 50)
    private String accountManagerContact;

    @Column(nullable = false, length = 50)             // 계정 상태
    private String accountStatus;


    //외래키
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true, foreignKey = @ForeignKey(name="fk_accounts_employee_id"))
    private EmployeeEntity employeeId;


    // 외부 외래키
    @OneToMany(mappedBy = "accountId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OpportunitiesEntity> opportunities;

    @OneToMany(mappedBy = "accountId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<ContractsEntity> contracts;

    @OneToMany(mappedBy = "accountId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<LeadsEntity> leads;

}
