package com.aivle.project.repository;

import com.aivle.project.entity.orders.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, String> {
//    @Query("SELECT o FROM OrderEntity o WHERE (:order_id IS NULL OR o.order_id = :order_id) " +
//            "AND (:contract_id IS NULL OR o.contract_id = :contract_id)")
//    List<OrdersEntity> findByOrderIdOrContractId(
//                    @Param("order_id") String order_id,
//                    @Param("contract_id") String contract_id
//            );

}
