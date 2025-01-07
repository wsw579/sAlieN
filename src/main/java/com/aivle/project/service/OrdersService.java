package com.aivle.project.service;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;

    // Create
    public void createOrder(OrdersDto dto) {
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setOrderDate(dto.getOrderDate());
        ordersEntity.setSalesDate(dto.getSalesDate());
        ordersEntity.setOrderAmount(dto.getOrderAmount());
        ordersEntity.setOrderStatus(dto.getOrderStatus());
        ordersEntity.setContractId(dto.getContractId());
        ordersEntity.setProductId(dto.getProductId());
        ordersEntity.setPartnerOpId(dto.getPartnerOpId());
        ordersRepository.save(ordersEntity);
    }

    // Read (모든 주문 조회)
    public List<OrdersEntity> readOrders() {
        return ordersRepository.findAll();
    }

    // Update
    public void updateOrder(Long orderId, OrdersDto dto) {
        OrdersEntity ordersEntity = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        ordersEntity.setOrderDate(dto.getOrderDate());
        ordersEntity.setSalesDate(dto.getSalesDate());
        ordersEntity.setOrderAmount(dto.getOrderAmount());
        ordersEntity.setOrderStatus(dto.getOrderStatus());
        ordersEntity.setContractId(dto.getContractId());
        ordersEntity.setProductId(dto.getProductId());
        ordersEntity.setPartnerOpId(dto.getPartnerOpId());
        ordersRepository.save(ordersEntity);
    }

    // Delete
    public void deleteOrder(Long orderId) {
        if (!ordersRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found");
        }
        ordersRepository.deleteById(orderId);
    }

    // Search (특정 주문 조회)
    public OrdersEntity searchOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
