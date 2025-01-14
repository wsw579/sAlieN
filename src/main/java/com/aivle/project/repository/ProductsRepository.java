package com.aivle.project.repository;

import com.aivle.project.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {
    // 제품을 삭제 상태로 표시하는 메서드
    @Modifying
    @Query("UPDATE ProductsEntity p SET p.productDeleted = true WHERE p.id = :productId")
    void softDeleteById(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE ProductsEntity p SET p.productDeleted = true WHERE p.id = :productId")
    void softDeleteAllById(@Param("productId") List<Long> productId);

    // 삭제되지 않은 제품만 조회하는 메서드
    @Query("SELECT p FROM ProductsEntity p WHERE p.productDeleted = false")
    List<ProductsEntity> findAllActive();
}
