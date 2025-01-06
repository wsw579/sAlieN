package com.aivle.project.dto.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductsResponseDto {
    private String productId;
    private String productName;
    private Float fixedPrice;
    private Float dealerPrice;
    private Float costPrice;
    private String productCondition; // Enum 대신 String으로 변환
    private String productDescription;
    private String productFamily;
}
