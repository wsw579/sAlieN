package com.aivle.project.service;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdersService {

    private final ContractsRepository contractsRepository;
    private final OrdersRepository ordersRepository;

    // Create
    public void createOrder(OrdersDto dto, ContractsEntity contract) {
        OrdersEntity orderEntity = new OrdersEntity();

        orderEntity.setOrderDate(dto.getOrderDate());
        orderEntity.setSalesDate(dto.getSalesDate());
        orderEntity.setOrderAmount(dto.getOrderAmount());
        orderEntity.setOrderStatus(dto.getOrderStatus());
        // ContractsEntity 설정
        if (contract != null) {
            orderEntity.setContract(contract);
        }
        orderEntity.setProductId(dto.getProductId());
        orderEntity.setPartnerOpId(dto.getPartnerOpId());
        ordersRepository.save(orderEntity);
    }

    // Read
    public List<OrdersEntity> readOrders() {
        return ordersRepository.findAll();
    }

    // Update
    @Transactional
    public void updateOrder(Long orderId, OrdersDto dto) {
        ContractsEntity contract = contractsRepository.findById(dto.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid contract ID"));

        OrdersEntity orderEntity = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        System.out.println("Before update: " + orderEntity);

        orderEntity.setOrderDate(dto.getOrderDate());
        orderEntity.setSalesDate(dto.getSalesDate());
        orderEntity.setOrderAmount(dto.getOrderAmount());
        orderEntity.setOrderStatus(dto.getOrderStatus());
        orderEntity.setContract(contract);
        orderEntity.setProductId(dto.getProductId());
        orderEntity.setPartnerOpId(dto.getPartnerOpId());
        ordersRepository.save(orderEntity);
    }

    // Delete
    public void deleteOrder(Long orderId) {
        ordersRepository.deleteById(orderId);
    }

    // Delete multiple orders by IDs
    public void deleteOrdersByIds(List<Long> ids) {
        ordersRepository.deleteAllById(ids);
    }

    // Search
    public OrdersEntity searchOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
