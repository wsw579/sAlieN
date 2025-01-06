package com.aivle.project.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponseDto {
    private String orderId;
    private Date orderDate;
    private Date salesDate;
    private Float orderAmount;
    private String orderStatus; // Enum 대신 String으로 변환
    private String contractId;
    private String productId;
    private String partnerOpId;
}
