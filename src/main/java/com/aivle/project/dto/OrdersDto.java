package com.aivle.project.dto;

import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.ProductsEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDto {
    @NotNull(message = "Order ID는 필수입니다.")
    private Long orderId;

    @NotNull(message = "주문 일자는 필수입니다.")
    @PastOrPresent(message = "주문 일자는 과거 또는 현재여야 합니다.")
    private LocalDate orderDate;

    @NotNull(message = "매출 일자는 필수입니다.")
    private LocalDate salesDate;

    @NotNull(message = "주문 수량은 필수입니다.")
    private float orderAmount;

    @NotBlank(message = "주문 상태는 필수입니다.")
    @Pattern(regexp = "^(none|draft|activated|completed|cancelled)$", message = "주문 상태는 'none', 'draft', 'activated', 'completed', 'cancelled' 중 하나여야 합니다.")
    private String orderStatus; // Enum 대신 String으로 변환
    // 외래키
    private ContractsEntity contractId;
    private ProductsEntity productId;
    private EmployeeEntity employeeId;
}
