package com.aivle.project.service;

import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.ProductCondition;
import com.aivle.project.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<ProductsEntity> readProducts() {
        return productsRepository.findAllActive();
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
        productsRepository.softDeleteById(productId);
    }

    // Delete multiple products by IDs
    public void deleteProductsByIds(List<Long> ids) {
        if (ids.size() == 1) {
            productsRepository.softDeleteById(ids.get(0));  // 단일 ID에 대해 개별 메서드 호출
        } else {
            productsRepository.softDeleteAllById(ids);  // 다중 ID에 대해 메서드 호출
        }
    }

    // Search
    public ProductsEntity searchProduct(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
