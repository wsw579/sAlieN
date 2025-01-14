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
@Table(name = "contracts")
public class ContractsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Column(nullable = false)
    private String contractStatus;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminationDate;

    @Column(nullable = true)
    private String contractDetail;

    @Column(nullable = false)
    private float contractSales;

    @Column(nullable = false)
    private float contractAmount;

    @Column(nullable = true)
    private String contractClassification;

    private boolean contractSelected;

    @Column(nullable = false)
    private boolean contractDeleted = false;


    // 외래키 부분

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employeeId;
//
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductsEntity productId;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private OpportunitiesEntity opportunityId;

    @OneToMany(mappedBy = "contractId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OrdersEntity> orders;



}
