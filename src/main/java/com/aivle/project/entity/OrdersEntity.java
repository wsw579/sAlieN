package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class OrdersEntity {
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

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private OrderStatus order_status;
    @Column(nullable = false)
    private String orderStatus;

//    @ManyToOne -> entity생성시 해당 코드로 변경
//    @JoinColumn(name = "contract_id", nullable = false)
//    private Contract contract;
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
    @Column(name = "product_id", nullable = false)
    private Long productId;

//    @ManyToOne
//    @JoinColumn(name = "partner_op_id", nullable = false)
//    private Partner_Op_Month partner_op_month
    @Column(name = "partner_op_id", nullable = false)
    private Long partnerOpId;
}
