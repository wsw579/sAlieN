package com.aivle.project.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Column(nullable = false)
    private String contractClassification;


    // 외래키 부분

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private OpportunitiesEntity opportunity;

//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private AccountsEntity account;
//
//    @ManyToOne
//    @JoinColumn(name = "personnel_id", nullable = false)
//    private PersonnelEntity personnel;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    private ProductsEntity product;

    //@ManyToOne
    //@JoinColumn
    //private OrderEntity orderId;



}
