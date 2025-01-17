package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Long accountId;

    @Column(nullable = false, length = 50)
    private String accountName;

    @Column(nullable = false, length = 50)
    private String accountType;

    @Column(nullable = true, length = 50)
    private String website;

    @Column(nullable = true, length = 50)
    private String contact;

    @Column(nullable = false, length = 50)
    private String businessType;

    @Column(nullable = false, length = 50)
    private String accountManager;

    @Column(nullable = true, length = 50)
    private String accountDetail;

    @Column(nullable = true, length = 50)
    private String address;

    @Column(nullable = false, length = 50)
    private String accountManagerContact;

    @Column(nullable = true, length = 50)
    private String accountStatus;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountCreatedDate;


    // 외래키 부분


    // 외부 외래키
    @OneToMany(mappedBy = "accountId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OpportunitiesEntity> opportunities;

    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL)
    private List<ContractsEntity> contracts;

    // 상위 계정을 참조하는 필드 (셀프 조인 ManyToOne)
    @ManyToOne
    @JoinColumn(name = "parent_account_id" , nullable = true) // 외래키 컬럼 이름
    private AccountEntity parentAccount;

    // 하위 계정들을 참조하는 필드 (셀프 조인 OneToMany)
    @OneToMany(mappedBy = "parentAccount", cascade = CascadeType.ALL)
    private List<AccountEntity> childAccounts;

    //@ManyToOne
    //@JoinColumn
    //private EmployeeEntity employeeId;

}


