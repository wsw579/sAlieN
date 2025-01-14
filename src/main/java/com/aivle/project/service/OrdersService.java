package com.aivle.project.service;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import com.aivle.project.repository.ProductsRepository;
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
    private final ProductsRepository productsRepository;

    // Create
    public void createOrder(OrdersDto dto, ContractsEntity contract) {
        OrdersEntity orderEntity = new OrdersEntity();

        ProductsEntity product = productsRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        orderEntity.setOrderDate(dto.getOrderDate());
        orderEntity.setSalesDate(dto.getSalesDate());
        orderEntity.setOrderAmount(dto.getOrderAmount());
        orderEntity.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        // ContractsEntity 설정
        if (contract != null) {
            orderEntity.setContract(contract);
        }
        orderEntity.setProduct(product);
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

        ProductsEntity product = productsRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        OrdersEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        System.out.println("Before update: " + order);

        order.setOrderDate(dto.getOrderDate());
        order.setSalesDate(dto.getSalesDate());
        order.setOrderAmount(dto.getOrderAmount());
        order.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        order.setContract(contract);
        order.setProduct(product);
        ordersRepository.save(order);
    }

    // Delete
    public void deleteOrder(Long orderId) {
        ordersRepository.deleteById(orderId);
    }

    // Delete multiple orders by IDs
    public void deleteOrdersByIds(List<Long> ids) {
        if (ids.size() == 1) {
            ordersRepository.deleteById(ids.get(0));  // 단일 ID에 대해 개별 메서드 호출
        } else {
            ordersRepository.deleteAllById(ids);  // 다중 ID에 대해 메서드 호출
        }
    }

    // Search
    public OrdersEntity searchOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
