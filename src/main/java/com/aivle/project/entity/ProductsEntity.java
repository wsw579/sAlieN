package com.aivle.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductsEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Float fixedPrice;

    @Column(nullable = false)
    private Float dealerPrice;

    @Column(nullable = false)
    private Float costPrice;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ProductCondition product_condition;
    @Column(nullable = false)
    private String productCondition;

    private String productDescription;

    private String productFamily;
}
