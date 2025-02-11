package com.aivle.project.dto;

import com.aivle.project.entity.EmployeeEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDto {
    @NotNull(message = "Product ID는 필수입니다.")
    private Long productId;

    @NotBlank(message = "제품명은 필수입니다.")
    private String productName;

    @NotNull(message = "정찰가는 필수입니다.")
    private float fixedPrice;

    @NotNull(message = "딜러가는 필수입니다.")
    private float dealerPrice;

    @NotNull(message = "원가는 필수입니다.")
    private float costPrice;

    @NotBlank(message = "제품 상태는 필수입니다.")
    @Pattern(regexp = "^(available|out_of_stock|discontinued)$", message = "제품 상태는 'available', 'out_of_stock', 'discontinued' 중 하나여야 합니다.")
    private String productCondition; // Enum 대신 String으로 변환

    @Size(max = 50, message = "제품 문의사항은 50자 이내로 입력해야 합니다.")
    private String productDescription;

    @Size(max = 50, message = "제품군은 50자 이내로 입력해야 합니다.")
    private String productFamily;

    // 외래키
    private EmployeeEntity employeeId;
}
