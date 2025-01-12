package com.aivle.project.entity;

import com.aivle.project.enums.ProductCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductsEntity implements Serializable {
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCondition productCondition;

    private String productDescription;

    private String productFamily;

    private boolean productSelected;

    @Column(nullable = false)
    private boolean productDeleted = false;
}
