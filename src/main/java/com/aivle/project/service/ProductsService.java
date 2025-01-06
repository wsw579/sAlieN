package com.aivle.project.service;

import com.aivle.project.dto.products.ProductsRequestDto;
import com.aivle.project.dto.products.ProductsResponseDto;
import com.aivle.project.entity.products.ProductsEntity;
import com.aivle.project.entity.products.ProductsStatus;
import com.aivle.project.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    // 제품 생성
    public void productsCreate(ProductsRequestDto requestDto) {
        ProductsEntity entity = toProductsEntity(requestDto);
        productsRepository.save(entity);
    }

    // 모든 제품 조회
    public List<ProductsResponseDto> getAllProducts() {
        List<ProductsEntity> products = productsRepository.findAll();
        return products.stream()
                .map(this::toProductsResponseDto)
                .collect(Collectors.toList());
    }

    // ID로 특정 제품 조회
    public ProductsResponseDto getProductById(String id) {
        ProductsEntity product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return toProductsResponseDto(product);
    }

    // 제품 수정
    @Transactional
    public void productsUpdate(String id, ProductsRequestDto requestDto) {
        ProductsEntity product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (requestDto.getProductName() != null) product.setProduct_name(requestDto.getProductName());
        if (requestDto.getFixedPrice() != null) product.setFixed_price(requestDto.getFixedPrice());
        if (requestDto.getDealerPrice() != null) product.setDealer_price(requestDto.getDealerPrice());
        if (requestDto.getCostPrice() != null) product.setCost_price(requestDto.getCostPrice());
        if (requestDto.getProductCondition() != null) {
            try {
                product.setProduct_condition(ProductsStatus.valueOf(requestDto.getProductCondition().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid product condition: " + requestDto.getProductCondition());
            }
        }
        if (requestDto.getProductDescription() != null) product.setProduct_description(requestDto.getProductDescription());
        if (requestDto.getProductFamily() != null) product.setProduct_family(requestDto.getProductFamily());

        productsRepository.save(product);
    }

    // 제품 삭제
    @Transactional
    public void productsDelete(String id) {
        if (!productsRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productsRepository.deleteById(id);
    }

    // Entity -> DTO 변환 메서드
    private ProductsResponseDto toProductsResponseDto(ProductsEntity product) {
        return new ProductsResponseDto(
                product.getProduct_id(),
                product.getProduct_name(),
                product.getFixed_price(),
                product.getDealer_price(),
                product.getCost_price(),
                product.getProduct_condition().name(),
                product.getProduct_description(),
                product.getProduct_family()
        );
    }

    // DTO -> Entity 변환 메서드
    private ProductsEntity toProductsEntity(ProductsRequestDto dto) {
        ProductsEntity entity = new ProductsEntity();
        entity.setProduct_name(dto.getProductName());
        entity.setFixed_price(dto.getFixedPrice());
        entity.setDealer_price(dto.getDealerPrice());
        entity.setCost_price(dto.getCostPrice());
        try {
            entity.setProduct_condition(ProductsStatus.valueOf(dto.getProductCondition().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid product condition: " + dto.getProductCondition());
        }
        entity.setProduct_description(dto.getProductDescription());
        entity.setProduct_family(dto.getProductFamily());
        return entity;
    }
}
