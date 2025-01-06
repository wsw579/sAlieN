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
public class OrdersRequestDto {
    private Date orderDate;
    private Date salesDate;
    private Float orderAmount;
    private String orderStatus; // 클라이언트가 Enum 대신 String으로 전달
    private String contractId;
    private String productId;
    private String partnerOpId;
}
