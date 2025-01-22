package com.aivle.project.dto;

import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.ProductsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDto {
    private Long orderId;
    private LocalDate orderDate;
    private LocalDate salesDate;
    private float orderAmount;
    private String orderStatus; // Enum 대신 String으로 변환
    // 외래키
    private ContractsEntity contractId;
    private ProductsEntity productId;
    private EmployeeEntity employeeId;
}
