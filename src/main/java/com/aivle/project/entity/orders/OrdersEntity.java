package com.aivle.project.entity.orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class OrdersEntity {
    @Id
    @Column(nullable = false)
    private String order_id;

    @PrePersist
    public void generateId() {
        if (this.order_id == null) { // ID가 없는 경우만 생성
            this.order_id = UUID.randomUUID().toString();
        }
    }

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date order_date;

    @Temporal(TemporalType.DATE)
    private Date sales_date;

    private Float order_amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrdersStatus order_status;

//    @ManyToOne -> entity생성시 해당 코드로 변경
//    @JoinColumn(name = "contract_id", nullable = false)
//    private Contract contract;
    @Column(name = "contract_id", nullable = false)
    private String contract_id;

//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
    @Column(name = "product_id", nullable = false)
    private String product_id;

//    @ManyToOne
//    @JoinColumn(name = "partner_op_id", nullable = false)
//    private Partner_Op_Month partner_op_month
    @Column(name = "partner_op_id", nullable = false)
    private String partner_op_id;
}
