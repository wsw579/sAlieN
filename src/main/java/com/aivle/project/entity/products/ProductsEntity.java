package com.aivle.project.entity.products;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import org.w3c.dom.Text;

import java.util.UUID;

public class ProductsEntity {
    @Id
    @Column(nullable = false)
    private String product_id;

    @PrePersist
    public void generateId() {
        if (this.product_id == null) { // ID가 없는 경우만 생성
            this.product_id = UUID.randomUUID().toString();
        }
    }

    @Column(nullable = false)
    private String product_name;

    @Column(nullable = false)
    private float fixed_price;

    @Column(nullable = false)
    private float dealer_price;

    @Column(nullable = false)
    private float cost_price;

    @Column(nullable = false)
    private Enum<ProductsStatus> product_condition;

    private Text product_description;

    private String product_family;
}
