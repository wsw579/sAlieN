package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    List<OrdersEntity> findByContract(ContractsEntity contract);

    // 주문을 삭제 상태로 표시하는 메서드
    @Modifying
    @Query("UPDATE OrdersEntity o SET o.orderDeleted = true WHERE o.id = :orderId")
    void softDeleteById(@Param("orderId") Long orderId);

    @Modifying
    @Query("UPDATE OrdersEntity o SET o.orderDeleted = true WHERE o.id = :orderId")
    void softDeleteAllById(@Param("orderId") List<Long> orderId);

    // 삭제되지 않은 제품만 조회하는 메서드
    @Query("SELECT o FROM OrdersEntity o WHERE o.orderDeleted = false")
    List<OrdersEntity> findAllActive();
}
