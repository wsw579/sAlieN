package com.aivle.project.service;

import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.ProductCondition;
import com.aivle.project.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    // Create
    public void createProduct(ProductsDto dto) {
        ProductsEntity productEntity = new ProductsEntity();

        productEntity.setProductName(dto.getProductName());
        productEntity.setFixedPrice(dto.getFixedPrice());
        productEntity.setDealerPrice(dto.getDealerPrice());
        productEntity.setCostPrice(dto.getCostPrice());
        productEntity.setProductCondition(ProductCondition.valueOf(dto.getProductCondition()));
        productEntity.setProductDescription(dto.getProductDescription());
        productEntity.setProductFamily(dto.getProductFamily());
        productsRepository.save(productEntity);
    }

    // Read
    public Page<ProductsEntity> readProducts(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            try {
                return productsRepository.findByProductIdLike(search, pageable);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 경우 빈 페이지 반환
                return Page.empty(pageable);
            }
        } else {
            return productsRepository.findAll(pageable);
        }
    }

    // Update
    @Transactional
    public void updateProduct(Long productId, ProductsDto dto) {
        ProductsEntity productEntity = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productEntity.setProductName(dto.getProductName());
        productEntity.setFixedPrice(dto.getFixedPrice());
        productEntity.setDealerPrice(dto.getDealerPrice());
        productEntity.setCostPrice(dto.getCostPrice());
        productEntity.setProductCondition(ProductCondition.valueOf(dto.getProductCondition()));
        productEntity.setProductDescription(dto.getProductDescription());
        productEntity.setProductFamily(dto.getProductFamily());
        productsRepository.save(productEntity);
    }

    // Delete
    public void deleteProduct(Long productId) {
        productsRepository.deleteById(productId);
    }

    // Delete multiple products by IDs
    public void deleteProductsByIds(List<Long> ids) {
        if (ids.size() == 1) {
            productsRepository.deleteById(ids.get(0));  // 단일 ID에 대해 개별 메서드 호출
        } else {
            productsRepository.deleteAllById(ids);  // 다중 ID에 대해 메서드 호출
        }
    }

    // Search
    public ProductsEntity searchProduct(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<ProductsDto> getAllProductIdsAndNames() {
        List<Object[]> results = productsRepository.findAllProductIdAndProductName();
        return results.stream()
                .map(result -> {
                    ProductsDto dto = new ProductsDto();
                    dto.setProductId((Long) result[0]);
                    dto.setProductName((String) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 상태 수 세기
    public Map<String, Long> getProductConditionCounts() {
        Map<String, Long> conditionCounts = new HashMap<>();
        List<Object[]> results = productsRepository.countProductsByCondition();

        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            conditionCounts.put(status, count);
        }

        return conditionCounts;
    }
}
