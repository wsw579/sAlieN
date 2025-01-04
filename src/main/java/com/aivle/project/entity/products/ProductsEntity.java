package com.aivle.project.entity.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
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
    private Float fixed_price;

    @Column(nullable = false)
    private Float dealer_price;

    @Column(nullable = false)
    private Float cost_price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductsStatus product_condition;

    private Text product_description;

    private String product_family;
}
