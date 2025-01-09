package com.aivle.project.entity;

import com.aivle.project.enums.OrderStatus;
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
@Table(name = "orders")
public class OrdersEntity implements Serializable {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate salesDate;

    private Float orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private ContractsEntity contract;
//    @Column(name = "contract_id", nullable = false)
//    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductsEntity product;

//    @ManyToOne
//    @JoinColumn(name = "partner_op_id", nullable = false)
//    private Partner_Op_Month partner_op_month
//    @Column(name = "partner_op_id", nullable = false)
//    private Long partnerOpId;
}
