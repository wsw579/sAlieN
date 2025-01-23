package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.ProductCondition;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(ContractsService.class);


    // Create
    public void createProduct(ProductsDto dto) {
        // 현재 사용자 정보 가져오기
        String currentUserId = UserContext.getCurrentUserId();
        System.out.println("현재 로그인된 사용자 ID: " + currentUserId);
        // 데이터베이스에서 EmployeeEntity 로드
        EmployeeEntity employee = employeeRepository.findByEmployeeId(currentUserId);

        ProductsEntity productEntity = new ProductsEntity();
        ProductsEntity productEntity = convertDtoToEntity(dto);

        productsRepository.save(productEntity);
    }

    // Read
    @Transactional(readOnly = true)
    public Page<ProductsEntity> readProducts(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            return productsRepository.findByProductIdLike("%" + search + "%", pageable);
        }
        return productsRepository.findAll(pageable);
    }

    // Update
    public void updateProduct(Long productId, ProductsDto dto) {
        ProductsEntity productEntity = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        updateEntityFromDto(productEntity, dto);
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

    // 테이블 데이터 전달
    @Transactional(readOnly = true)
    public Map<String, Object> getProductPageData(int page, int size, String search, String sortColumn, String sortDirection) {
        Page<ProductsEntity> productsPage = readProducts(page, size, search, sortColumn, sortDirection);
        Map<String, Long> conditionCounts = getProductConditionCounts();

        Map<String, Object> data = new HashMap<>();
        data.put("productsPage", productsPage);
        data.put("conditionCounts", conditionCounts);
        data.put("totalCount", conditionCounts.values().stream().mapToLong(Long::longValue).sum());

        return data;
    }

    // 상태 수 세기
    @Transactional(readOnly = true)
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

    // 헬퍼 메서드
    private ProductsEntity convertDtoToEntity(ProductsDto dto) {
        ProductsEntity productsEntity = new ProductsEntity();
        updateEntityFromDto(productsEntity, dto);
        return productsEntity;
    }

    private void updateEntityFromDto(ProductsEntity entity, ProductsDto dto) {
        entity.setProductName(dto.getProductName());
        entity.setFixedPrice(dto.getFixedPrice());
        entity.setDealerPrice(dto.getDealerPrice());
        entity.setCostPrice(dto.getCostPrice());
        entity.setProductCondition(ProductCondition.valueOf(dto.getProductCondition()));
        entity.setProductDescription(dto.getProductDescription());
        entity.setProductFamily(dto.getProductFamily());
        productsRepository.save(entity);
    }

    private ProductsDto convertIdToDto(Long id) {
        ProductsDto dto = new ProductsDto();
        dto.setProductId(id);
        return dto;
    }
}
