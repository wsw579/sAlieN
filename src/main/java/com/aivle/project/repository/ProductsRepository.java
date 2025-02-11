package com.aivle.project.repository;

import com.aivle.project.entity.ProductsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {
    @Query("SELECT p.productId, p.productName FROM ProductsEntity p")
    List<Object[]> findAllProductIdAndProductName();

    @Query("SELECT p FROM ProductsEntity p WHERE p.productName LIKE %:productName%")
    Page<ProductsEntity> findByProductNameLike(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT CAST(p.productCondition AS string), COUNT(p) FROM ProductsEntity p GROUP BY p.productCondition")
    List<Object[]> countProductsByCondition();

    // "AI"가 포함된 제품의 개수
    @Query("SELECT COUNT(p) FROM ProductsEntity p WHERE LOWER(p.productFamily) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    long countProductsByProductFamilyContaining(@Param("keyword") String keyword);
}
