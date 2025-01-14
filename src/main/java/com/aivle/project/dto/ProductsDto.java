package com.aivle.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDto {
    private Long productId;
    private String productName;
    private float fixedPrice;
    private float dealerPrice;
    private float costPrice;
    private String productCondition; // Enum 대신 String으로 변환
    private String productDescription;
    private String productFamily;
    private boolean productSelected;
    private boolean productDeleted = false;
}
