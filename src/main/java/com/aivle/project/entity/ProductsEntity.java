package com.aivle.project.entity;

import com.aivle.project.enums.ProductCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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

    // 외부 외래키
    @OneToMany(mappedBy = "productId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OpportunitiesEntity> opportunities;

    @OneToMany(mappedBy = "productId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH)
    private List<ContractsEntity> contracts;
  
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OrdersEntity> orders;
}
