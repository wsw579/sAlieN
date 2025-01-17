package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    List<OrdersEntity> findByContractId(ContractsEntity contractId);
    Page<OrdersEntity> findByOrderStatusContainingIgnoreCase(String search, Pageable pageable);

    @Query("SELECT CAST(o.orderStatus AS string), COUNT(o) FROM OrdersEntity o GROUP BY o.orderStatus")
    List<Object[]> countOrdersByStatus();

    // 차트 그래프
    @Query("SELECT MONTH(o.orderDate), COUNT(o) " +
            "FROM OrdersEntity o " +
            "WHERE YEAR(o.orderDate) = :year AND o.orderStatus = 'activated' " +
            "GROUP BY MONTH(o.orderDate)")
    List<Object[]> getMonthlyOrders(@Param("year") int year);
}
