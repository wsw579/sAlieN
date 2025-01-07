package com.aivle.project.service;

import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ProductsEntity;
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
        ProductsEntity productsEntity = new ProductsEntity();
        productsEntity.setProductName(dto.getProductName());
        productsEntity.setFixedPrice(dto.getFixedPrice());
        productsEntity.setDealerPrice(dto.getDealerPrice());
        productsEntity.setCostPrice(dto.getCostPrice());
        productsEntity.setProductCondition(dto.getProductCondition());
        productsEntity.setProductDescription(dto.getProductDescription());
        productsEntity.setProductFamily(dto.getProductFamily());
        productsRepository.save(productsEntity);
    }

    // Read (모든 제품 조회)
    public List<ProductsEntity> readProducts() {
        return productsRepository.findAll();
    }

    // Update
    public void updateProduct(Long productId, ProductsDto dto) {
        ProductsEntity productsEntity = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productsEntity.setProductName(dto.getProductName());
        productsEntity.setFixedPrice(dto.getFixedPrice());
        productsEntity.setDealerPrice(dto.getDealerPrice());
        productsEntity.setCostPrice(dto.getCostPrice());
        productsEntity.setProductCondition(dto.getProductCondition());
        productsEntity.setProductDescription(dto.getProductDescription());
        productsEntity.setProductFamily(dto.getProductFamily());

        productsRepository.save(productsEntity);
    }

    // Delete
    public void deleteProduct(Long productId) {
        if (!productsRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        productsRepository.deleteById(productId);
    }

    // Search (특정 제품 조회)
    public ProductsEntity searchProduct(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
