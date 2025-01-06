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
public class ProductsRequestDto {
    private String productName;
    private Float fixedPrice;
    private Float dealerPrice;
    private Float costPrice;
    private String productCondition; // 클라이언트가 Enum 대신 String으로 전달
    private String productDescription;
    private String productFamily;
}
